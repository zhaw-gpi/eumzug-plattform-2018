package ch.zhaw.gpi.eumzugplattform.restcontroller;

import ch.zhaw.gpi.eumzugplattform.entities.Person;
import ch.zhaw.gpi.eumzugplattform.entities.Status;
import ch.zhaw.gpi.eumzugplattform.entities.TransactionLogEntry;
import ch.zhaw.gpi.eumzugplattform.repositories.PersonRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ch.zhaw.gpi.eumzugplattform.repositories.StatusRepository;
import ch.zhaw.gpi.eumzugplattform.repositories.TransactionLogEntryRepository;

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
    private TransactionLogEntryRepository transactionLogEntryRepository;
        
    // Verdrahten des Person Repository
    @Autowired
    private PersonRepository personRepository;
    
    // Verdrahten des Status Repository
    @Autowired
    private StatusRepository statusRepository;
    
    // Konstante für die Ressourcen-URL
    private static final String ENDPOINT = "/umzugapi/v1/transactionlogs";
    
    /**
     * GET: Liste aller Einträge
     *
     * @return 
     */
    @RequestMapping(path = ENDPOINT)
    public List<TransactionLogEntry> getTransactionLogList(){
        // Liste von Transaktionslog-Einträgen suchen und zurückgeben
        return transactionLogEntryRepository.findAll();
    }
    
    /**
     * GET: Den aktuellsten Status-Eintrag (status und logTimeStamp) für eine bestimmte Person ausgeben 
     * @param personId
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{personId}/latest")
    public ResponseEntity<?> findMostRecentTransactionLogByPerson(@PathVariable String personId){
        // Person aus Datenbank auslesen
        Optional<Person> person = personRepository.findById(personId);
        
        // Falls Person vorhanden
        if (person.isPresent()){
            // Den aktuellsten Status-Eintrag (status und logTimeStamp) einer Person suchen
            Optional<TransactionLogEntry> searchedTransactionLogEntry = transactionLogEntryRepository.findTopByPersonOrderByLogTimeStampDesc(person.get());
            
            // Falls ein Eintrag gefunden wurde
            if(searchedTransactionLogEntry.isPresent()){
                return new ResponseEntity<TransactionLogEntry>(searchedTransactionLogEntry.get(), HttpStatus.OK);
            }
        }
        
        // Ansonsten NOT-FOUND zurück geben
        return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
    }
    
    /**
     * GET: Liste aller Transaktionslog-Einträge mit einem bestimmten Status sortiert nach logTimeStamp
     * 
     * Achtung: in der Realität wäre viel wünschenswerter, nur diejenigen Einträge zu erhalten, welche noch aktuell sind, also zu denen nicht bereits ein neuerer Eintrag mit einem anderen Status existieren
     * 
     * @param statusName
     * @return 
     */
    @RequestMapping(path = ENDPOINT + "/{statusName}")
    public ResponseEntity<?> getTransactionLogListByStatus(@PathVariable String statusName){
        // Status aus Datenbank auslesen
        Optional<Status> status = statusRepository.findByName(statusName);
        
        // Wenn kein Status gefunden wurde
        if(!status.isPresent()){
            // ... NOT-FOUND zurück geben
            return new ResponseEntity<HttpStatus>(HttpStatus.NOT_FOUND);
        }
        
        // Liste der Transaktionslog-Einträge auslesen
        List<TransactionLogEntry> searchedTransactionLogEntries = transactionLogEntryRepository.findByStatusOrderByLogTimeStampDesc(status.get());
        
        // Liste zurückgeben
        return new ResponseEntity<List<TransactionLogEntry>>(searchedTransactionLogEntries, HttpStatus.OK);
    }
}
