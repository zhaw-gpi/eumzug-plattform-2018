package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.processdata.CheckBaseInsuranceResult;
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
 * Die Prüfung selbst erfolgt über den CheckBaseInsuranceService.
 * 
 * Die Aufgabe dieses Delegates ist:
 * - Aus person in der Prozessvariable personList die Eigenschaften baseInsuranceNumber, 
 * firstName, officialName und dateOfBirth auslesen.
 * - Die Methode checkBaseInsurance des CheckBaseInsuranceService mit diesen Angaben füttern.
 * - Die Antwort als CheckBaseInsuranceResult-Objekt auslesen. Sie enthält zwei String-Variablen: 
 *   - checkBaseInsuranceResult, welche positiv (Yes, Person ist grundversichert), 
 * negativ (No, Person ist nicht grundversichert) oder unklar (Unknown, die Prüfung
 * konnte nicht durchgeführt werden. Einerseits sind nicht alle Versicherungen abfragbar (Bsp.: Helsana) oder die Versichertenkartennum-mer konnte nicht gefunden werden.) sein kann.
 *   - checkBaseInsuranceResultDetails mit weiteren Erläuterungen.
 * - Diese Antwort wird in den gleich lautenden Eigenschaften des Person-Objekts übergeben.
 * - Die personList-Prozessvariable wird mit der veränderten personList überschrieben.
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
        
        // Methode des CheckBaseInsuranceService aufrufen mit den erforderlichen Prüf-Angaben
        CheckBaseInsuranceResult checkBaseInsuranceResult = checkBaseInsuranceService.checkBaseInsuranceValidity(
                person.getBaseInsuranceNumber(),
                person.getFirstName(),
                person.getOfficialName(),
                person.getDateOfBirth());
        
        // Die Antwort den passenden Person-Variablen zuweisen
        person
            .setCheckBaseInsuranceResult(checkBaseInsuranceResult.getCheckResult())
            .setCheckBaseInsuranceResultDetails(checkBaseInsuranceResult.getCheckResultDetails());
        
        // Die personList-Prozessvariable wird mit der veränderten personList überschrieben
        delegateExecution.setVariable("personList", personList);
    }
}
