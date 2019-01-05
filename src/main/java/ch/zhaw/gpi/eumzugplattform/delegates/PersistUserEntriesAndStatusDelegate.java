package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.entities.Person;
import ch.zhaw.gpi.eumzugplattform.entities.Status;
import ch.zhaw.gpi.eumzugplattform.entities.TransactionLogEntry;
import ch.zhaw.gpi.eumzugplattform.repositories.PersonRepository;
import java.util.Date;
import java.util.Optional;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import ch.zhaw.gpi.eumzugplattform.repositories.StatusRepository;
import ch.zhaw.gpi.eumzugplattform.repositories.TransactionLogEntryRepository;

/**
 * TransactionLogEntry-Eintrag in Umzugsplattform-DB einfügen

 Über ein JPA-Repository wird basierend auf der Prozessvariable localPersonId
 ein Person-Objekt aus der Datenbank gesucht. Wurde keines gefunden,
 wird eine neue Person angelegt mit den verschiedenen
 Prozessvariablen-Inhalten zur Personenidentifikation.

 Nun wird ein TransactionLogEntry-Objekt angelegt mit dem Verweis auf das
 Person-Objekt, mit der aktuellen Uhrzeit und dem status.
 */
@Named("persistUserEntriesAndStatusAdapter")
public class PersistUserEntriesAndStatusDelegate implements JavaDelegate {

    // Die Referenzen auf die Person-, TransactionLog- und StatusRepository-Instanzen erhalten
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TransactionLogEntryRepository transactionLogEntryRepository;
    @Autowired
    private StatusRepository statusRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Person-Objekt initialisiern
        Person personEntity;

        // Für die Suche nach der Person erforderliche Prozessvariablen localPersonId auslesen
        String localPersonId = (String) execution.getVariable("localPersonId");

        // Die Person mit der Id localPersonId finden
        Optional<Person> personResult = personRepository.findById(localPersonId);

        // Prüfen, ob ein Result zurückgegeben wurde
        if (personResult.isPresent()) {
            // Falls ja, dann das Resultat der Person-Variablen zuordnen
            personEntity = personResult.get();
        } else {
            // Falls nein, eine neue Person anlegen mit den entsprechenden Daten:
            // Personenidentifikationsmerkmale-Prozessvariablen in lokale Variablen auslesen
            Long vn = (Long) execution.getVariable("vn");
            String firstName = (String) execution.getVariable("firstName");
            String officialName = (String) execution.getVariable("officialName");
            Date dateOfBirth = (Date) execution.getVariable("dateOfBirth");
            Integer sex = (Integer) execution.getVariable("sex");

            // Ein neues Person-Objekt erstellen
            Person personNeu = new Person();

            // Die Eigenschaften dieses Objekts auf die passenden lokalen Variablen setzen
            personNeu
                    .setLocalPersonId(localPersonId)
                    .setDateOfBirth(dateOfBirth)
                    .setFirstName(firstName)
                    .setOfficialName(officialName)
                    .setSex(sex);
            // AHV-Nummer ist optional und soll daher nur dann gesetzt werden, wenn vorhanden
            if (vn != null) {
                personNeu.setVn(vn);
            }

            // Das neue Person-Objekt persistieren und zurückgeben
            personEntity = personRepository.save(personNeu);
        }

        // Status-Variable auslesen
        String statusName = (String) execution.getVariable("status");
        
        // Zugehörigen Status finden
        Optional<Status> status = statusRepository.findByName(statusName);
        
        // Ein neues TransaktionsLog-Objekt mit den relevanten Angaben inkl. aktuellem Datum/Uhrzeit erstellen
        TransactionLogEntry transactionLogEntry = new TransactionLogEntry();
        transactionLogEntry
                .setLogTimeStamp(new Date())
                .setPerson(personEntity)
                .setStatus(status.get());

        // Das neue TransaktionsLog-Objekt persistieren
        transactionLogEntryRepository.save(transactionLogEntry);
    }
}
