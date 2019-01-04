package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.StateEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Status-Entität, welche CRUD-Operationen auf die
 * dahinterliegende Datenbank kapselt
 * 
 * @author Stefan Fischer und scep
 */
public interface StateRepository extends JpaRepository<StateEntity, Long>{
    
// Methodendeklaration, um einen Status basierend auf Namen zu erhalten
    Optional<StateEntity> findByName(String name);
}
