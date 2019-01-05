package ch.zhaw.gpi.eumzugplattform.repositories;

import ch.zhaw.gpi.eumzugplattform.entities.DocumentType;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentType;
import ch.zhaw.gpi.eumzugplattform.entities.Municipality;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Klasse für MunicipalityDocumentType-Entität, welche CRUD-Operationen auf die
 * dahinterliegende Datenbank kapselt
 * 
 * @author Stefan Fischer und scep
 */
public interface MunicipalityDocumentTypeRepository extends JpaRepository<MunicipalityDocumentType, Long>{
    // Deklaration einer Methode, um Beziehungseinträge basierend auf einem Dokument zu finden
    List<MunicipalityDocumentType> findByDocumentType(DocumentType documentType);
    
    // Deklaration einer Methode, um Beziehungseinträge basierend auf einer Gemeinde zu finden
    List<MunicipalityDocumentType> findByMunicipality(Municipality municipality);
}
