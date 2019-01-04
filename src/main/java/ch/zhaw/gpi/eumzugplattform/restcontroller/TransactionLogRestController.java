package ch.zhaw.gpi.eumzugplattform.restcontroller;

import ch.zhaw.gpi.eumzugplattform.entities.PersonEntity;
import ch.zhaw.gpi.eumzugplattform.entities.StateEntity;
import ch.zhaw.gpi.eumzugplattform.entities.TransactionLogEntity;
import ch.zhaw.gpi.eumzugplattform.repositories.PersonRepository;
import ch.zhaw.gpi.eumzugplattform.repositories.StateRepository;
import ch.zhaw.gpi.eumzugplattform.repositories.TransactionLogRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Kontroller Klasse, welche die FunktionalitÃ¤ten implementiert um
 * TransaktionsLogs über REST zu verwalten
 * 
 * @author Stefan Fischer und scep
 */
@RestController
public class TransactionLogRestController {
    // Verdrahten des TransactionLog Repository
    @Autowired
    private TransactionLogRepository transactionLogRepository;
        
    // Verdrahten des Person Repository
    @Autowired
    private PersonRepository personRepository;
    
    // Verdrahten des Status Repository
    @Autowired
    private StateRepository stateRepository;
    
    // Konstante für die Ressourcen-URL
    private static final String ENDPOINT = "/umzugapi/v1/transactionlogs";
    
    /**
     * GET: Liste aller Einträge
     *
     * @return 
     */
    @RequestMapping(path = ENDPOINT)
    public List<TransactionLogEntity> getTransactionLogList(){
        // Liste von Transaktionslog-Einträgen suchen und zurückgeben
        return transactionLogRepository.findAll();
    }
    
    /**
     * GET: Den aktuellsten Status-Eintrag (status und logTimeStamp) für eine bestimmte Person ausgeben 
     * @param personId
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{personId}/latest")
    public ResponseEntity<Optional<TransactionLogEntity>> findMostRecentTransactionLogByPerson(@PathVariable String personId){
        // Person aus Datenbank auslesen
        Optional<PersonEntity> person = personRepository.findById(personId);
        
        // Falls Person vorhanden
        if (person.isPresent()){
            // Den aktuellsten Status-Eintrag (status und logTimeStamp) einer Person suchen
            Optional<TransactionLogEntity> searchedTransactionLogEntry = transactionLogRepository.findTopByPersonOrderByLogTimeStampDesc(person.get());
            
            // Falls ein Eintrag gefunden wurde
            if(searchedTransactionLogEntry.isPresent()){
                return new ResponseEntity(searchedTransactionLogEntry.get(), HttpStatus.OK);
            }
        }
        
        // Ansonsten NOT-FOUND zurück geben
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
    
    /**
     * GET: Liste aller Transaktionslog-Einträge mit einem bestimmten Status sortiert nach logTimeStamp
     * 
     * Achtung: in der Realität wäre viel wünschenswerter, nur diejenigen Einträge zu erhalten, welche noch aktuell sind, also zu denen nicht bereits ein neuerer Eintrag mit einem anderen Status existieren
     * 
     * @param stateName
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{stateName}")
    public ResponseEntity<List<TransactionLogEntity>> getTransactionLogListByState(@PathVariable String stateName){
        // Status aus Datenbank auslesen
        Optional<StateEntity> state = stateRepository.findByName(stateName);
        
        // Wenn kein Status gefunden wurde
        if(!state.isPresent()){
            // ... NOT-FOUND zurück geben
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        
        // Liste der Transaktionslog-Einträge auslesen
        List<TransactionLogEntity> searchedTransactionLogs = transactionLogRepository.findByStateOrderByLogTimeStampDesc(state.get());
        
        // Liste zurückgeben
        return new ResponseEntity(searchedTransactionLogs, HttpStatus.OK);
    }
}
