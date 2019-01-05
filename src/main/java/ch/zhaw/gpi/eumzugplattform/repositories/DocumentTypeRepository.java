package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.DocumentType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Dokument-Entität, welche CRUD-Operationen auf die
 * dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer>{
    // Methodendeklaration, um ein Dokument basierend auf Namen zu erhalten
    Optional<DocumentType> findByName(String name);
}
