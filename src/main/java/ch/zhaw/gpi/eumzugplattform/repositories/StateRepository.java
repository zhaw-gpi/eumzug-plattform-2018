package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.StateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Status-Entität, welche CRUD-Operationen auf die
 * dahinterliegende Datenbank kapselt
 * 
 * @author Stefan Fischer
 */
public interface StateRepository extends JpaRepository<StateEntity, Long>{
    // Status nach Namen
    StateEntity findByName(String name);
}
