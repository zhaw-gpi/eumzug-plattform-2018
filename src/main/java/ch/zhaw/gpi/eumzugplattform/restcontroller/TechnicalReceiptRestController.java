package ch.zhaw.gpi.eumzugplattform.restcontroller;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ch.zhaw.gpi.eumzugplattform.processdata.TechnicalReceipt;

/**
 * REST-Controller
 * 
 * @author scep
 */
@RestController
public class TechnicalReceiptRestController {

    @Autowired
    private RuntimeService runtimeService;
    
    /**
     * REST-Ressource für URL /umzugapi/v1/technicalreceipt/ (POST)
     * 
     * @return HTTP-Response mit einem Status 200
     */
    @PostMapping(value = "/umzugapi/v1/technicalreceipt/")
    public ResponseEntity<HttpStatus> addTechnicalReceipt(@RequestBody TechnicalReceipt technicalReceipt) {        
        // Nachricht zurück an Prozessinstanz übergeben
        try {
            runtimeService.createMessageCorrelation("EksAntwort")
            .processInstanceBusinessKey(technicalReceipt.getBusinessKey())
            .setVariable("responseCode", technicalReceipt.getResponseCode())
            .correlate();

            // Positiven Status zurückgeben
            return new ResponseEntity<HttpStatus>(HttpStatus.OK);   
        } catch (Exception e) {
            // Negativen Status zurückgeben
            return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);   
        }
    }
}
