package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.TransactionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für TransactionLog-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface TransactionLogRepository extends JpaRepository<TransactionLogEntity, Long>{
    
}
