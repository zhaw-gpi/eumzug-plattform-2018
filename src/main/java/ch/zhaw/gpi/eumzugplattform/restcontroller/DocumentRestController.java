package ch.zhaw.gpi.eumzugplattform.restcontroller;

import ch.zhaw.gpi.eumzugplattform.entities.DocumentType;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentType;
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
import ch.zhaw.gpi.eumzugplattform.repositories.DocumentTypeRepository;
import ch.zhaw.gpi.eumzugplattform.repositories.MunicipalityDocumentTypeRepository;

/**
 * Kontroller Klasse, welche die Funktionalitäten implementiert um
 * Dokumenttypen über REST zu verwalten
 * 
 * @author Stefan Fischer und scep
 */
@RestController
public class DocumentRestController {
    // Verdrahten der benötigten Repositories
    @Autowired
    private DocumentTypeRepository documentTypeRepository;
    
    @Autowired
    private MunicipalityDocumentTypeRepository municipalityDocumentTypeRepository;
    
    // Konstante für die Ressourcen-URL
    private static final String ENDPOINT = "/umzugapi/v1/documents";
    
    /**
     * GET: Liste aller Dokumenttypen
     * 
     * @return
     */
    @RequestMapping(path = ENDPOINT)
    public ResponseEntity<List<DocumentType>> getDocumentTypeList(){
        // Liste von Dokumente suchen und zurückgeben
        return new ResponseEntity(documentTypeRepository.findAll(), HttpStatus.OK);
    }
    
    /**
     * GET: Erhalten eines Dokumenttypen über seinen Namen
     * 
     * @param name
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{name}")
    public ResponseEntity<DocumentType> getDocumentTypeByName(@PathVariable String name){
        // Dokument suchen in Datenbank
        Optional<DocumentType> documentType = documentTypeRepository.findByName(name);
        
        // Wenn Dokumenttyp existiert
        if(documentType.isPresent()){
            // ... dieses zurückgeben mit Status OK
            return new ResponseEntity(documentType.get(),HttpStatus.OK);
        } else {
            // ... ansonsten NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * POST: Hinzufügen eines Dokumententyp
     * 
     * @param documentType
     * @return 
     */
    @RequestMapping(path = ENDPOINT, method = RequestMethod.POST)
    public ResponseEntity<DocumentType> addDocumentType(@Valid @RequestBody DocumentType documentType){
        // Dokumenttyp suchen in Datenbank
        Optional<DocumentType> searchedDocumentType = documentTypeRepository.findById(documentType.getId());
        
        // Wenn Dokumenttyp existiert
        if(searchedDocumentType.isPresent()){
            // ... CONFLICT-Status zurück geben
            return new ResponseEntity(HttpStatus.CONFLICT);
        } else {
            try{
                // ... den neuen Dokumenttyp in der Datenbank hinzuzufügen
                DocumentType savedDocumentType = documentTypeRepository.save(documentType);
                
                // und zurück geben
                return new ResponseEntity(savedDocumentType,HttpStatus.OK);
            } catch (DataIntegrityViolationException e){
                // Falls es zu einer Verletzung einer Bedingung kommt (nur Name ist möglich, entweder leer oder bereits bestehend), dann CONFLICT-Status zurückgeben
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
        }
    }
    
    /**
     * PUT: Umbenennen eines Dokumententyp
     * 
     * @param id            Id des Dokumententyp
     * @param name          Neuer Name des Dokumententyp
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{id}/{name}", method = RequestMethod.PUT)
    public ResponseEntity<DocumentType> renameDocumentType(@PathVariable Integer id, @PathVariable String name){
        // Dokumenttyp in der Datenbank suchen
        Optional<DocumentType> searchedDocumentType = documentTypeRepository.findById(id);
        
        // Wenn Dokumenttyp gefunden wurde ...
        if(searchedDocumentType.isPresent()){
            // Den Namen aktualisieren
            searchedDocumentType.get().setName(name);
            
            // Den gespeicherten Dokumenttyp zurückgeben
            return new ResponseEntity(documentTypeRepository.save(searchedDocumentType.get()), HttpStatus.OK);
        } else {
            // ... ansonsten NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * DELETE: Löschen eines Dokumententyps sofern in keiner Beziehung mehr genutzt
     * 
     * @param id    Id des zu löschenden Dokumententyps
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteDocumentType(@PathVariable Integer id){
        // Dokumenttyp suchen in Datenbank
        Optional<DocumentType> documentTypeToDelete = documentTypeRepository.findById(id);
        
        // Falls Dokumenttyp nicht vorhanden ist ...
        if (!documentTypeToDelete.isPresent()){
            // ... NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        // Kurzreferenz auf zu löschenden Dokumenttyp setzen
        DocumentType documentType = documentTypeToDelete.get();
        
        // Abhängigkeiten laden
        List<MunicipalityDocumentType> references = municipalityDocumentTypeRepository.findByDocumentType(documentType);
        
        // Falls Abhängigkeiten bestehen
        if (!references.isEmpty()){
            // ... CONFLICT-Status zurück geben
            return new ResponseEntity(HttpStatus.CONFLICT);
        }
        
        // Dokumenttyp löschen
        documentTypeRepository.delete(documentType);
        
        // Erfolgsmeldung zurück geben
        return new ResponseEntity(HttpStatus.OK);
    }
}
