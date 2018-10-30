package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für Municipality-Entität, welche CRUD-Operationen auf die dahinterliegende Datenbank kapselt
 * 
 * @author scep
 */
public interface MunicipalityRepository extends JpaRepository<MunicipalityEntity, Integer> {
        
}
