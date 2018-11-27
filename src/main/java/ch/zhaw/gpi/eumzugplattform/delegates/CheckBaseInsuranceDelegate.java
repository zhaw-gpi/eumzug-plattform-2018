package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.processdata.Person;
import ch.zhaw.gpi.eumzugplattform.processdata.PersonList;
import ch.zhaw.gpi.eumzugplattform.services.CheckBaseInsuranceService;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Grundversicherungsprüfung pro umziehender Person im Auskunftsdienst des VeKa-Centers
 * 
 * Aufgrund der Komplexität soll die eigentliche Prüfung in einem ausgelagerten Service stattfinden (CheckBaseInsuranceService). In der Delegate-Klasse soll lediglich die aktuelle Person in der Multi-Instanz-Aktivität ermittelt werden und diese an den CheckBaseInsuranceService übergeben werden, um dort geprüft zu werden.
 * 
 * @author scep
 */
@Named("checkBaseInsuranceAdapter")
public class CheckBaseInsuranceDelegate implements JavaDelegate {
    
    // Verdrahten des CheckBaseInsuranceService
    @Autowired
    CheckBaseInsuranceService checkBaseInsuranceService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        // PersonenListe und loopCounter-Prozessvariablen auslesen
        PersonList personList = (PersonList) delegateExecution.getVariable("personList");
        Integer loopCounter = (Integer) delegateExecution.getVariable("loopCounter");
        
        // Person innerhalb der PersonenListe basierend auf dem Index (loopCounter) ermitteln
        Person person = personList.getPersons().get(loopCounter);
        
        // Methode des CheckBaseInsuranceService aufrufen mit der zu prüfenden Person
        // Darin wird die als Referenz übergebene Person verändert und somit
        // automatisch auch personList, welche diese Person enthält
        // Weil auch diese wiederum nur eine Referenz auf die entsprechende Variable in delegateExecution ist
        // wird automatisch auch die Prozessvariable entsprechend angepasst
        checkBaseInsuranceService.checkBaseInsuranceValidity(person);
    }
}
