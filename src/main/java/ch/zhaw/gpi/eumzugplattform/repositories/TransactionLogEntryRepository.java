package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.Person;
import ch.zhaw.gpi.eumzugplattform.entities.Status;
import ch.zhaw.gpi.eumzugplattform.entities.TransactionLogEntry;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für TransactionLog-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface TransactionLogEntryRepository extends JpaRepository<TransactionLogEntry, Long>{
    // Methodendeklaration, um den letzten Transaktions-Log-Eintrag einer Person zu erhalten
    Optional<TransactionLogEntry> findTopByPersonOrderByLogTimeStampDesc(Person personEntity);
    
    // Methodendeklaration, um alle Transaktions-Log-Einträge zu einem Status zu erhalten, absteigend sortiert nach Logzeit
    List<TransactionLogEntry> findByStatusOrderByLogTimeStampDesc(Status status);
}
