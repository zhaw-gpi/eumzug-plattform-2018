package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Person-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface PersonRepository extends JpaRepository<Person, String>{
    
}
