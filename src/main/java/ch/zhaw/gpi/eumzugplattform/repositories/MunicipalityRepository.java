package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.Municipality;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Municipality-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface MunicipalityRepository extends JpaRepository<Municipality, Integer> {
    
    // Methodendeklaration, um ein Dokument basierend auf Namen zu erhalten
    Optional<Municipality> findByMunicipalityName(String name);
}
