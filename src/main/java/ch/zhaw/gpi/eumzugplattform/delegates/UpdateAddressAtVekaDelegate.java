package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.processdata.PersonList;
import ch.zhaw.gpi.eumzugplattform.services.UpdateAddressAtVekaService;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mitteilen der Adressänderung für alle umziehenden Personen an das VeKa-Center
 * 
 * Aufgrund der Komplexität soll die eigentliche Funktionalität in einem ausgelagerten Service stattfinden (UpdateAddressAtVekaService), dessen Anforderungen gleich anschliessend beschrieben sind. In der Delegate-Klasse soll lediglich die Personen-Liste und die Wegzugsadress-Angaben aus den Prozessvariablen gelesen werden und diese an den UpdateAddressAtVekaService übergeben werden.
 * @author scep
 */
@Named("updateAddressAtVekaAdapter")
public class UpdateAddressAtVekaDelegate implements JavaDelegate {
    
    // Verdrahten des UpdateAddressAtVekaService
    @Autowired
    private UpdateAddressAtVekaService updateAddressAtVekaService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        // Relevante Prozessvariablen auslesen (Personenliste, Zuzugsangaben)
        PersonList personList = (PersonList) delegateExecution.getVariable("personList");
        String street = (String) delegateExecution.getVariable("streetMoveIn");
        String houseNumber = (String) delegateExecution.getVariable("houseNumberMoveIn");
        Integer plz = (Integer) delegateExecution.getVariable("swissZipCodeMoveIn");
        String town = (String) delegateExecution.getVariable("townMoveIn");
        
        // Methode des UpdateAddressAtVekaService aufrufen mit der Personenliste und Adress-Angaben
        updateAddressAtVekaService.updateAddressForPersons(personList, street, houseNumber, plz, town);
    }
}
