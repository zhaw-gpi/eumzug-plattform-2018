package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.entities.PersonEntity;
import ch.zhaw.gpi.eumzugplattform.entities.TransactionLogEntity;
import ch.zhaw.gpi.eumzugplattform.repositories.PersonRepository;
import ch.zhaw.gpi.eumzugplattform.repositories.TransactionLogRepository;
import java.util.Date;
import java.util.Optional;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * TransactionLogEntity-Eintrag in Umzugsplattform-DB einfügen
 *
 * Über ein JPA-Repository wird basierend auf der Prozessvariable localPersonId
 * ein PersonEntity-Objekt aus der Datenbank gesucht. Wurde keines gefunden,
 * wird eine neue PersonEntity angelegt mit den verschiedenen
 * Prozessvariablen-Inhalten zur Personenidentifikation.
 *
 * Nun wird ein TransactionLogEntity-Objekt angelegt mit dem Verweis auf das
 * PersonEntity-Objekt, mit der aktuellen Uhrzeit und dem status.
 */
@Named("persistUserEntriesAndStatusAdapter")
public class PersistUserEntriesAndStatusDelegate implements JavaDelegate {

    // Die Referenzen auf die PersonEntity- und TransactionLogRepository-Instanzen erhalten
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TransactionLogRepository transactionLogRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Person-Objekt initialisiern
        PersonEntity personEntity;

        // Für die Suche nach der PersonEntity erforderliche Prozessvariablen localPersonId auslesen
        String localPersonId = (String) execution.getVariable("localPersonId");

        // Die PersonEntity mit der Id localPersonId finden
        Optional<PersonEntity> personResult = personRepository.findById(localPersonId);

        // Prüfen, ob ein Result zurückgegeben wurde
        if (personResult.isPresent()) {
            // Falls ja, dann das Resultat der PersonEntity-Variablen zuordnen
            personEntity = personResult.get();
        } else {
            // Falls nein, eine neue Person anlegen mit den entsprechenden Daten:
            // Personenidentifikationsmerkmale-Prozessvariablen in lokale Variablen auslesen
            Long vn = (Long) execution.getVariable("vn");
            String firstName = (String) execution.getVariable("firstName");
            String officialName = (String) execution.getVariable("officialName");
            Date dateOfBirth = (Date) execution.getVariable("dateOfBirth");
            Integer sex = (Integer) execution.getVariable("sex");

            // Ein neues PersonEntity-Objekt erstellen
            PersonEntity personNeu = new PersonEntity();

            // Die Eigenschaften dieses Objekts auf die passenden lokalen Variablen setzen
            personNeu.setLocalPersonId(localPersonId);
            personNeu.setDateOfBirth(dateOfBirth);
            personNeu.setFirstName(firstName);
            personNeu.setOfficialName(officialName);
            personNeu.setSex(sex);
            // AHV-Nummer ist optional und soll daher nur dann gesetzt werden, wenn vorhanden
            if (vn != null) {
                personNeu.setVn(vn);
            }

            // Das neue PersonEntity-Objekt persistieren und zurückgeben
            personEntity = personRepository.save(personNeu);
        }

        // Ein neues TransaktionsLog-Objekt erstellen
        TransactionLogEntity transactionLog = new TransactionLogEntity();

        // Aktuelle Uhrzeit der logTimeStamp-Eigenschaft zuweisen
        transactionLog.setLogTimeStamp(new Date());

        // Status-Variable auslesen und der passenden Transaktionslog-Eigenschaft zuweisen
        String status = (String) execution.getVariable("status");
        transactionLog.setStatus(status);

        // Die gefundene oder neu erstellte PersonEntity der passenden Transaktionslog-Eigenschaft zuweisen
        transactionLog.setPerson(personEntity);

        // Das neue TransaktionsLog-Objekt persistieren
        transactionLogRepository.save(transactionLog);
    }
}
