package ch.zhaw.gpi.eumzugplattform.restcontroller;

import ch.zhaw.gpi.eumzugplattform.processdata.PersonPD;
import ch.zhaw.gpi.eumzugplattform.services.CheckBaseInsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller-Klasse, welche den Zugriff per REST auf die CheckBaseInsurance-Klasse erlaubt
 * 
 * @author Stefan Fischer und scep
 */
@RestController
public class CheckBaseInsuranceRestController {
    
    // Einbinden des Service checkBaseInsurance
    @Autowired
    private CheckBaseInsuranceService checkBaseInsuranceService;
    
    // Konstante für die Ressourcen-URL
    private static final String ENDPOINT = "/umzugapi/v1/checkbaseinsurance";
    
    @RequestMapping(path = ENDPOINT, method = RequestMethod.PUT)
    public ResponseEntity<PersonPD> checkBaseInsurance(@RequestBody PersonPD personToCheck){
        // Grundversicherung von Person überprüfen
        checkBaseInsuranceService.checkBaseInsuranceValidity(personToCheck);
        
        // Grundversicherung überprüft und Resultat zurückmelden
        return new ResponseEntity(personToCheck, HttpStatus.OK); 
    }
}
