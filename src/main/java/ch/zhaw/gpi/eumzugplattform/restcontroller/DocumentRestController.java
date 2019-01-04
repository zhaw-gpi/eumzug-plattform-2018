package ch.zhaw.gpi.eumzugplattform.restcontroller;

import ch.zhaw.gpi.eumzugplattform.entities.DocumentEntity;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentRelationEntity;
import ch.zhaw.gpi.eumzugplattform.repositories.DocumentRepository;
import ch.zhaw.gpi.eumzugplattform.repositories.MunicipalityDocumentRelationRepository;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kontroller Klasse, welche die Funktionalitäten implementiert um
 * Dokumente über REST zu verwalten
 * 
 * @author Stefan Fischer und scep
 */
@RestController
public class DocumentRestController {
    // Verdrahten des Document Repository
    @Autowired
    private DocumentRepository documentRepository;
    
    // Verdrahten des MunicipalityDocumentRelation Repository
    @Autowired
    private MunicipalityDocumentRelationRepository municipalityDocumentRelationRepository;
    
    // Konstante für die Ressourcen-URL
    private static final String ENDPOINT = "/umzugapi/v1/documents";
    
    /**
     * GET: Liste aller Dokumente
     * 
     * @return
     */
    @RequestMapping(path = ENDPOINT)
    public ResponseEntity<List<DocumentEntity>> getDocumentList(){
        // Liste von Dokumente suchen und zurückgeben
        return new ResponseEntity(documentRepository.findAll(), HttpStatus.OK);
    }
    
    /**
     * GET: Erhalten eines Dokuments über seinen Namen
     * 
     * @param name
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{name}")
    public ResponseEntity<DocumentEntity> getDocumentByName(@PathVariable String name){
        // Dokument suchen in Datenbank
        Optional<DocumentEntity> document = documentRepository.findByName(name);
        
        // Wenn Dokument existiert
        if(document.isPresent()){
            // ... dieses zurückgeben mit Status OK
            return new ResponseEntity(document.get(),HttpStatus.OK);
        } else {
            // ... ansonsten NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * POST: Hinzufügen eines Dokumentes
     * 
     * @param document
     * @return 
     */
    @RequestMapping(path = ENDPOINT, method = RequestMethod.POST)
    public ResponseEntity<DocumentEntity> addDocument(@Valid @RequestBody DocumentEntity document){
        // Dokument suchen in Datenbank
        Optional<DocumentEntity> searchedDocument = documentRepository.findById(document.getDocumentId());
        
        // Wenn Dokument existiert
        if(searchedDocument.isPresent()){
            // ... CONFLICT-Status zurück geben
            return new ResponseEntity(HttpStatus.CONFLICT);
        } else {
            try{
                // ... das neue Dokument in der Datenbank hinzuzufügen
                DocumentEntity savedDocument = documentRepository.save(document);
                
                // und zurück geben
                return new ResponseEntity(savedDocument,HttpStatus.OK);
            } catch (DataIntegrityViolationException e){
                // Falls es zu einer Verletzung einer Bedingung kommt (nur Name ist möglich, entweder leer oder bereits bestehend), dann CONFLICT-Status zurückgeben
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
        }
    }
    
    /**
     * PUT: Umbenennen eines Dokumentes
     * 
     * @param id    Id des Dokuments
     * @param name          Neuer Name des Dokuments (Bezeichnung)
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{id}/{name}", method = RequestMethod.PUT)
    public ResponseEntity<DocumentEntity> renameDocument(@PathVariable Integer id, @PathVariable String name){
        // Dokument in der Datenbank suchen
        Optional<DocumentEntity> searchedDocument = documentRepository.findById(id);
        
        // Wenn Dokument gefunden wurde ...
        if(searchedDocument.isPresent()){
            // Den Namen aktualisieren
            searchedDocument.get().setName(name);
            
            // Das gespeicherte Dokument zurückgeben
            return new ResponseEntity(documentRepository.save(searchedDocument.get()), HttpStatus.OK);
        } else {
            // ... ansonsten NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * DELETE: Löschen eines Dokuments sofern in keiner Beziehung mehr genutzt
     * 
     * @param id    Id des zu löschenden Dokuments
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteDocument(@PathVariable Integer id){
        // Dokument suchen in Datenbank
        Optional<DocumentEntity> documentToDelete = documentRepository.findById(id);
        
        // Falls Dokument nicht vorhanden ist ...
        if (!documentToDelete.isPresent()){
            // ... NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        // Kurzreferenz auf zu löschendes Dokument setzen
        DocumentEntity document = documentToDelete.get();
        
        // Abhängigkeiten laden
        List<MunicipalityDocumentRelationEntity> references = municipalityDocumentRelationRepository.findByDocumentEntity(document);
        
        // Falls Abhängigkeiten bestehen
        if (!references.isEmpty()){
            // ... CONFLICT-Status zurück geben
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        
        // Dokument löschen
        documentRepository.delete(document);
        
        // Erfolgsmeldung zurück geben
        return new ResponseEntity(HttpStatus.OK);
    }
}
