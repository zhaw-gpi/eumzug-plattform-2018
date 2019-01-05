package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.processdata.FilesOfPerson;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentType;
import ch.zhaw.gpi.eumzugplattform.processdata.MunicipalityDocTypeFile;
import ch.zhaw.gpi.eumzugplattform.entities.Municipality;
import ch.zhaw.gpi.eumzugplattform.processdata.FilesOfPersonList;
import ch.zhaw.gpi.eumzugplattform.processdata.PersonList;
import ch.zhaw.gpi.eumzugplattform.processdata.PersonPD;
import ch.zhaw.gpi.eumzugplattform.repositories.MunicipalityRepository;
import java.util.List;
import java.util.Optional;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import ch.zhaw.gpi.eumzugplattform.repositories.MunicipalityDocumentTypeRepository;

/**
 * Serialisierte Prozessvariable mit relevanten Dokumenten basierend auf Angaben
 in der Umzugsplattform-Datenbank erstellen

 Über ein Data Access Object sollen die Dokumente der relevanten
 Municipality-Objekte aus der Datenbank in ein List Objekt eingelesen
 werden.
 * 
 * Von dieser Liste wird für jeden Eintrag ein Objekt der Klasse MunicipalityDocTypeFile
 erzeugt, welches einerseits den Eintrag erhält, aber auch zwei Eigenschaften fileDataUrl
 und fileName, welche dann im "Dokumente hochladen"-User Task mit Inhalt gefüllt wird.
 
 Damit keine Deserialisierungs-Probleme auftreten (Details siehe in JavaDoc der Klasse
 FilesOfPerson), benötigt es ein Hilfsobjekt der Klasse FilesOfPerson.
 
 Dieses Hilfsobjekt wird mittels Camunda Spin in ein JSON-Objekt
 serialisiert und der Prozessvariable documents zugewiesen. Der Prozessvariable
 documentsExist wird mit true/fals mitgegeben, ob Dokumente existieren.
 */
@Named("getDocumentsAdapter")
public class GetDocumentsDelegate implements JavaDelegate {

    // Die für Datenbankzugriff benötigten Repositories werden verdrahtet
    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Autowired
    private MunicipalityDocumentTypeRepository municipalityDocumentTypeRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Prozess Variable personList wird der Variable personList zugewiesen.
        PersonList personList = (PersonList) execution.getVariable("personList");

        // Werte von der Variable personList der Liste persons zuweisen
        List<PersonPD> persons = personList.getPersons();

        // Prozess Variable municipalityIdMoveIn wird der Variable municipalityIdMoveIn zugewiesen.
        int municipalityIdMoveIn = (int) execution.getVariable("municipalityIdMoveIn");

        // Gemeinde wird anhand der Id aus der Datenbank gelesen
        Optional<Municipality> moveInMunicipalityResult = municipalityRepository.findById(municipalityIdMoveIn);

        // Variable für MunicipalityDocumentType-Objekte initialisieren
        List<MunicipalityDocumentType> municipalityDocumentTypes;

        // Die Variable documentsExist wird mit false initialisiert
        Boolean documentsExist = false;
        
        // Ein Dateien pro Person-Objekt instanzieren
        FilesOfPerson municipalityDocTypeFileList = new FilesOfPerson();
        
        // Ein Dateien pro Person-Liste-Objekt instanzieren
        FilesOfPersonList filesOfPersonList = new FilesOfPersonList();

        // Prüfen, ob auch wirklich eine gültige Gemeinde zurückgegeben wurde
        if (moveInMunicipalityResult.isPresent()) {
            // Falls ja, alle DokumentenTypen für diese Gemeinde erhalten
            municipalityDocumentTypes = municipalityDocumentTypeRepository.findByMunicipality(moveInMunicipalityResult.get());

            // Prüfen ob die municipalityDocuments Liste nicht leer ist. 
            // Nur falls mind. 1 Dokument in der Liste vorhanden ist, ...
            if (!municipalityDocumentTypes.isEmpty()) {    
                // ... wird die Variable documentsExist auf true gesetzt
                documentsExist = true;    
                
                // ... und die Datei pro Gemeinde-Liste gefüllt
                for (MunicipalityDocumentType municipalityDocumentType : municipalityDocumentTypes) {
                    MunicipalityDocTypeFile municipalityDocTypeFile = new MunicipalityDocTypeFile();
                    municipalityDocTypeFile.setMunicipalityDocumentType(municipalityDocumentType);
                    municipalityDocTypeFileList.addMunicipalityDocTypeFile(municipalityDocTypeFile);
                }
                
                // ... und die soeben erstelle Liste jeder Person zugewiesen und der Dateien pro Person-Liste hinzugefügt
                for(PersonPD person : persons) {
                    FilesOfPerson filesOfPerson = new FilesOfPerson();
                    filesOfPerson.setPerson(person);
                    filesOfPerson.setMunicipalityDocTypeFiles(municipalityDocTypeFileList.getMunicipalityDocTypeFiles());
                    filesOfPersonList.addFilesOfPerson(filesOfPerson);
                }
            }
        } // Falls nicht, dann muss nichts gemacht werden, weil die entsprechenden Variablen schon gesetzt sind

        // Die erstellte Liste einer Prozessvariable zuweisen
        execution.setVariable("documentsPerPerson", filesOfPersonList);
        
        // Die Variable documentsExists einer Prozessvariable zuweisen
        execution.setVariable("documentsExist", documentsExist);
    }
}
