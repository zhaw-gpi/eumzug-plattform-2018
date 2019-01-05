package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.Status;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Status-Entität, welche CRUD-Operationen auf die
 * dahinterliegende Datenbank kapselt
 * 
 * @author Stefan Fischer und scep
 */
public interface StatusRepository extends JpaRepository<Status, Long>{
    
// Methodendeklaration, um einen Status basierend auf Namen zu erhalten
    Optional<Status> findByName(String name);
}
