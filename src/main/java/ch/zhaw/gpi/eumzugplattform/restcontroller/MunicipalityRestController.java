package ch.zhaw.gpi.eumzugplattform.restcontroller;

import ch.zhaw.gpi.eumzugplattform.entities.Municipality;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentType;
import ch.zhaw.gpi.eumzugplattform.repositories.MunicipalityRepository;
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
import ch.zhaw.gpi.eumzugplattform.repositories.MunicipalityDocumentTypeRepository;

/**
 * Kontroller Klasse, welche die Funktionalitäten implementiert um
 * Gemeinden über REST zu verwalten
 * 
 * Ohne die Möglichkeit, Dokument-Beziehungen zu erstellen/anzupassen
 * 
 * @author Stefan Fischer und scep
 */
@RestController
public class MunicipalityRestController {
    // Verdrahten des Gemeinde Repositories
    @Autowired
    private MunicipalityRepository municipalityRepository;
    
    // Verdrahten des MunicipalityDocumentRelation Repository
    @Autowired
    private MunicipalityDocumentTypeRepository municipalityDocumentRelationRepository;
    
    // Konstante für die Ressourcen-URL
    private static final String ENDPOINT = "/umzugapi/v1/municipalities";
    
    /**
     * GET: Liste aller Gemeinden
     *
     * @return 
     */
    @RequestMapping(path = ENDPOINT)
    public List<Municipality> getMunicipalityList(){
        // Liste von Gemeinden suchen und zurückgeben
        return municipalityRepository.findAll();
    }
    
    /**
     * GET: Gemeinde über ihren Namen zurück geben
     * 
     * @param name
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{name}")
    public ResponseEntity<Municipality> getMunicipalityByName(@PathVariable String name){
        // Gemeinde nach Name suchen
        Optional<Municipality> municipality = municipalityRepository.findByMunicipalityName(name);
        
        // Wenn Gemeinde existiert
        if(municipality.isPresent()){
            // ... diese zurückgeben mit Status OK
            return new ResponseEntity(municipality.get(),HttpStatus.OK);
        } else {
            // ... ansonsten NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * POST: Hinzufügen einer Gemeinde
     * 
     * @param municipality
     * @return 
     */
    @RequestMapping(path = ENDPOINT, method = RequestMethod.POST)
    public ResponseEntity<Municipality> addMunicipality(@Valid @RequestBody Municipality municipality){
        // Gemeinde suchen in Datenbank
        Optional<Municipality> searchedMunicipality = municipalityRepository.findById(municipality.getMunicipalityId());
        
        // Wenn Gemeinde existiert
        if(searchedMunicipality.isPresent()){
            // ... CONFLICT-Status zurück geben
            return new ResponseEntity(HttpStatus.CONFLICT);
        } else {
            try{
                // ... die neue Gemeinde in der Datenbank hinzuzufügen
                Municipality savedMunicipality = municipalityRepository.save(municipality);
                
                // und zurück geben
                return new ResponseEntity(savedMunicipality,HttpStatus.OK);
            } catch (DataIntegrityViolationException e){
                // Falls es zu einer Verletzung einer Bedingung kommt
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
        }
    }
    
    /**
     * PUT: Ändern einer Gemeinde (z.B. neue Gebührenwerte)
     * 
     * @param municipality      Gemeinde mit neuen Daten
     * @return 
     */
    @RequestMapping(path = ENDPOINT, method = RequestMethod.PUT)
    public ResponseEntity<Municipality> updateMunicipality(@RequestBody @Valid Municipality municipality){
        // Gemeinde in der Datenbank suchen
        Optional<Municipality> searchedMunicipality = municipalityRepository.findById(municipality.getMunicipalityId());
        
        // Wenn Gemeinde gefunden wurde ...
        if(searchedMunicipality.isPresent()){
            // Erhaltene Gemeinde persistieren
            Municipality savedMunicipality = municipalityRepository.save(municipality);
            
            // Die gespeicherte Gemeinde zurückgeben
            return new ResponseEntity(savedMunicipality, HttpStatus.OK);
        } else {
            // ... ansonsten NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
    
    /**
     * DELETE: Löschen einer Gemeinde und zugehöriger Beziehungen
     * 
     * @param id    Id der zu löschenden Gemeinde
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMunicipality(@PathVariable Integer id){
        // Gemeinde suchen in Datenbank
        Optional<Municipality> municipalityToDelete = municipalityRepository.findById(id);
        
        // Falls Gemeinde nicht vorhanden ist ...
        if (!municipalityToDelete.isPresent()){
            // ... NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        // Kurzreferenz auf zu löschende Gemeinde setzen
        Municipality municipality = municipalityToDelete.get();
        
        // Abhängigkeiten laden
        List<MunicipalityDocumentType> references = municipalityDocumentRelationRepository.findByMunicipality(municipality);
        
        // Falls Abhängigkeiten bestehen
        if (!references.isEmpty()){
            // Über diese iterieren
            references.forEach((relation) -> {
                // ... und die Beziehung entfernen
                municipalityDocumentRelationRepository.delete(relation);
            });
        }
        
        // Gemeinde löschen
        municipalityRepository.delete(municipality);
        
        // Erfolgsmeldung zurück geben
        return new ResponseEntity(HttpStatus.OK);
    }
}
