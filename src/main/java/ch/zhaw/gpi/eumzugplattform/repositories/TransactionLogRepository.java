package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.PersonEntity;
import ch.zhaw.gpi.eumzugplattform.entities.StateEntity;
import ch.zhaw.gpi.eumzugplattform.entities.TransactionLogEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für TransactionLog-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface TransactionLogRepository extends JpaRepository<TransactionLogEntity, Long>{
    // Methodendeklaration, um den letzten Transaktions-Log-Eintrag einer Person zu erhalten
    Optional<TransactionLogEntity> findTopByPersonOrderByLogTimeStampDesc(PersonEntity personEntity);
    
    // Methodendeklaration, um alle Transaktions-Log-Einträge zu einem Status zu erhalten, absteigend sortiert nach Logzeit
    List<TransactionLogEntity> findByStateOrderByLogTimeStampDesc(StateEntity state);
}
