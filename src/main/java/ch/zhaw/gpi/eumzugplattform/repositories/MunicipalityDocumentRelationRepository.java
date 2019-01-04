package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.DocumentEntity;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentRelationEntity;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für MunicipalityDocumentRelation-Entität, welche CRUD-Operationen auf die
 * dahinterliegende Datenbank kapselt
 * 
 * @author Stefan Fischer und scep
 */
public interface MunicipalityDocumentRelationRepository extends JpaRepository<MunicipalityDocumentRelationEntity, Long>{
    // Deklaration einer Methode, um Beziehungseinträge basierend auf einem Dokument zu finden
    List<MunicipalityDocumentRelationEntity> findByDocumentEntity(DocumentEntity document);
    
    // Deklaration einer Methode, um Beziehungseinträge basierend auf einer Gemeinde zu finden
    List<MunicipalityDocumentRelationEntity> findByMunicipalityEntity(MunicipalityEntity municipality);
}
