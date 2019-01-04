package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.DocumentEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Dokument-Entität, welche CRUD-Operationen auf die
 * dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer>{
    // Methodendeklaration, um ein Dokument basierend auf Namen zu erhalten
    Optional<DocumentEntity> findByName(String name);
}
