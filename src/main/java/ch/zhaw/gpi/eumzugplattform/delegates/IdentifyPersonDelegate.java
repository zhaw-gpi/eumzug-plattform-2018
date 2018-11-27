package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.ech.xmlns.ech_0007._5.SwissMunicipalityType;
import ch.ech.xmlns.ech_0044._4.DatePartiallyKnownType;
import ch.ech.xmlns.ech_0044._4.NamedPersonIdType;
import ch.ech.xmlns.ech_0044._4.PersonIdentificationType;
import ch.ech.xmlns.ech_0194._1.InfoType;
import ch.ech.xmlns.ech_0194._1.PersonMoveRequest;
import ch.ech.xmlns.ech_0194._1.PersonMoveResponse;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityEntity;
import static ch.zhaw.gpi.eumzugplattform.helpers.DateConversionHelper.DateToXMLGregorianCalendar;
import ch.zhaw.gpi.eumzugplattform.processdata.Person;
import ch.zhaw.gpi.eumzugplattform.processdata.PersonList;
import ch.zhaw.gpi.eumzugplattform.services.LocalPersonIdGeneratorService;
import ch.zhaw.gpi.eumzugplattform.webserviceclients.ResidentRegisterWebServiceClient;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * JavaDelegate für Personidentifikation im kantonalen Personenregister
 * 
 * Die Operation identifyPerson des Personenregister-Web Service (http://localhost:8083/soap/PersonenRegisterService) wird synchron aufgerufen. Der eigentliche Web Service-Client (ResidentRegisterWebServiceClient) für die Kommunikation mit diesem Service ist in einer eigenen Klasse enthalten.
 * 
 * Die Aufgabe dieser Klasse ist zunächst, die folgenden Prozessvariablen den passenden Eigenschaften der personMoveRequest-Klasse, respektive einer ihrer referenzierten Klassen zuzuweisen: businessCaseId, municipalityNameMoveOut, municipalityIdMoveOut, firstName, officialName, sex, dateOfBirth, vn (optional), localPersonId und eine neue Variable personIdCategory mit dem Wert LOC.UMZUGPLATTFORM.
 * 
 * Hinweis: Die personMoveRequest und weitere eCH-Klassen werden gebildet über ein Build-Plugin, das im pom.xml konfiguriert ist.
 * 
 * Mit der personMoveRequest-Klasse kann nun der ResidentRegisterWebServiceClient die Methode identifyPerson ausführen, welche als Antwort ein Objekt der Klasse PersonMoveResponse zurückgibt.
 * 
 * Womit wir bei der zweiten Aufgabe dieser Klasse sind, nämlich dem Interpretieren der personMoveRespone: Es werden die zwei in personMoveResponse enthaltenen Eigenschaften personKnown und sofern vorhanden moveAllowed den gleich benannten Prozessvariablen übergeben.
 * 
 * @author scep
 */
@Named("IdentifyPersonAdapter")
public class IdentifyPersonDelegate implements JavaDelegate {

    @Autowired
    private ResidentRegisterWebServiceClient personenRegisterClient;
    
    @Autowired
    private LocalPersonIdGeneratorService localPersonIdGeneratorService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String businessCaseId = (String) execution.getVariable("businessCaseId");

        MunicipalityEntity municipalityEntity = (MunicipalityEntity) execution.getVariable("municipalityMoveOut");

        String firstName = (String) execution.getVariable("firstName");
        String officialName = (String) execution.getVariable("officialName");
        Integer sex = (Integer) execution.getVariable("sex");
        Date dateOfBirth = (Date) execution.getVariable("dateOfBirth");
        Long vn = (Long) execution.getVariable("vn");
        String localPersonId = (String) execution.getVariable("localPersonId");

        SwissMunicipalityType swissMunicipality = new SwissMunicipalityType();
        swissMunicipality.setMunicipalityId(municipalityEntity.getMunicipalityId());
        swissMunicipality.setMunicipalityName(municipalityEntity.getMunicipalityName());

        NamedPersonIdType namedPersonId = new NamedPersonIdType();
        namedPersonId.setPersonIdCategory("LOC.UMZUGPLATTFORM");
        namedPersonId.setPersonId(localPersonId);

        XMLGregorianCalendar xMLGregorianCalendar = DateToXMLGregorianCalendar(dateOfBirth);

        DatePartiallyKnownType datePartiallyKnown = new DatePartiallyKnownType();
        datePartiallyKnown.setYearMonthDay(xMLGregorianCalendar);

        PersonIdentificationType personIdentification = new PersonIdentificationType();
        personIdentification.setDateOfBirth(datePartiallyKnown);
        personIdentification.setFirstName(firstName);
        personIdentification.setOfficialName(officialName);
        personIdentification.setSex(sex.toString());
        if (vn != null) {
            personIdentification.setVn(BigInteger.valueOf(vn));
        }
        personIdentification.setLocalPersonId(namedPersonId);

        PersonMoveRequest personMoveRequest = new PersonMoveRequest();
        personMoveRequest.setMunicipality(swissMunicipality);
        personMoveRequest.setPersonIdentification(personIdentification);

        Object personenRegisterResponse = personenRegisterClient.identifyPerson(personMoveRequest, businessCaseId);
        
        if(personenRegisterResponse instanceof InfoType) {
            throw new BpmnError("NegativeReportErhalten", ((InfoType) personenRegisterResponse).getTextGerman());
        }
        
        PersonMoveResponse personMoveResponse = (PersonMoveResponse) personenRegisterResponse;

        Boolean personKnown = personMoveResponse.isPersonKnown();
        execution.setVariable("personKnown", personKnown);
        
        if(personKnown){
            BigInteger moveAllowedBigInteger = personMoveResponse.getMoveAllowed();
            if(moveAllowedBigInteger != null){
                // Wenn Person umzugsberechtigt ist, dann muss moveAllowed von
                // personMoveResponse den Wert 1 haben
                Boolean moveAllowed = moveAllowedBigInteger.equals(BigInteger.valueOf(1L));
                execution.setVariable("moveAllowed", moveAllowed);     
                
                // Es wird eine Liste initialisiert, um den Meldepflichtigen (und allenfalls mitumziehende Personen) aufzunehmen
                PersonList personList = new PersonList();
                
                // Der Meldepflichtige wird als neue Person erfasst und der Liste hinzugefügt
                Person meldePflichtiger = new Person();
                meldePflichtiger
                        .setDateOfBirth(dateOfBirth)
                        .setFirstName(firstName)
                        .setOfficialName(officialName)
                        .setLocalPersonId(localPersonId)
                        .setSex(sex.toString())
                        .setIsMainPerson(true);
                personList.addPerson(meldePflichtiger);

                /** 
                 * Wenn Person umzugsberechtigt ist, werden die mitumziehenden Personen 
                 * ausgelesen und ebenfalls der Liste hinzugefügt
                 */
                if(moveAllowed) {
                    // Auslesen der mitumziehenden Personen in eine temporäre Liste
                    List<PersonMoveResponse.RelatedPerson> relatedPersons = personMoveResponse.getRelatedPerson();
                    
                    // Iterieren über jeden Eintrag in dieser Liste
                    for(PersonMoveResponse.RelatedPerson relatedPerson : relatedPersons){
                        // Ein neues Person-Objekt anlegen
                        Person relative = new Person();
                        
                        // Die Eigenschaften dieser Person setzen
                        PersonIdentificationType personIdentificationType = relatedPerson.getPersonIdentification();
                        
                        relative
                            .setDateOfBirth(personIdentificationType.getDateOfBirth().getYearMonthDay().toGregorianCalendar().getTime())
                            .setFirstName(personIdentificationType.getFirstName())
                            .setOfficialName(personIdentificationType.getOfficialName())
                            .setSex(personIdentificationType.getSex());
                        
                        // Die AHV-Nummer kann nicht direkt ausgelesen werden (BigInteger)
                        Long vnTemp = (personIdentificationType.getVn() != null ? personIdentificationType.getVn().longValue() : null);
                        // Die localPersonId wird generiert wie beim Meldepflichtigen mit dem localPersonIdGeneratorService
                        relative
                            .setLocalPersonId(
                                localPersonIdGeneratorService.generateId(
                                    vnTemp, relative.getFirstName(), relative.getOfficialName(), relative.getDateOfBirth(), relative.getSex()
                                ));
                        
                        // Person der PersonenListe hinzufügen
                        personList.addPerson(relative);
                    }                    

                    // Wenn nicht bloss der Meldepflichtige in der Liste ist, dann relativesExist auf wahr setzen
                    Boolean relativesExist = false;
                    if(personList.getPersons().size() > 1) {
                        relativesExist = true;
                    }

                    // Schreiben der Prozessvariablen personList und relativesExist
                    // PS: Diese Variable wird unter anderem in MitumziehendePersonenErfassenForm
                    // in einer serialisierten (JSON)-Form benötigt. Dies wird im Hintergrund
                    // automatisch von Camunda Spin erledigt, weil in application.properties
                    // die Eigenschaft default-serialization-format auf application/json gesetzt ist
                    execution.setVariable("personList", personList);
                    execution.setVariable("relativesExist", relativesExist);
                }
            }
        }
    }
}
