package ch.zhaw.gpi.eumzugplattform.services;

import ch.zhaw.gpi.eumzugplattform.processdata.VeKaCard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Service für die Umzugsplattform, welche mit dem VeKa-Center-Auskunftsdienst kommuniziert
 * 
 * Die Implementation richtet sich nach https://spring.io/guides/gs/consuming-rest/
 * 
 * @author scep
 */
@Component
public class VeKaClientService {
    
    // Deklarieren einer Spring Web RestTemplate-Variable, welches als Rest-Client fungiert
    private final RestTemplate restTemplate;
    
    // Auslesen der REST-Server-Endpoint-Adresse aus einer application.properties-Umgebungsvariable
    @Value("${veka.endpoint}")
    private String vekaEndpoint;

    // Constructor der Klasse, bei welchem ein neues RestTemplate-Objekt instanziert wird
    public VeKaClientService() {
        restTemplate = new RestTemplate();
    }
    
    /**
     * Methode, um die Karteninformationen zu einer Kartennummer zu erhalten
     * 
     * @param baseInsuranceNumber   Versicherungskartennummer
     * @return                      null oder ein Versichertenkarten-Objekt
     */
    public VeKaCard getVeKaCard(Long baseInsuranceNumber) {        
        // Aufruf des REST-Services über die getForObject-Methode von RestTemplate, welche als Parameter erwartet:
        // - Die URL mit Parametern, wobei der in {} gesetzte Teil dann durch den Parameter baseInsuranceNumber ersetzt wird
        // - Die Klassendefinition, in welcher die Antwort aufbereitet werden soll (von JSON zu dieser Klasse)
        // - Eine passende Anzahl an Parameter-Werten, welche die {} in der URL ersetzt (hier nur einer)
        // Zurücknehmen der Ressource und Deserialisierung als VeKaCard-Objekt
        try{
            // Versuchen, eine Karte zu erhalten
            VeKaCard veKaCard = restTemplate.getForObject(
                    vekaEndpoint + "/cards/{baseInsuranceNumber}",
                    VeKaCard.class,
                    baseInsuranceNumber);
            
            // Wenn es klappt, diese zurückgeben
            return veKaCard;
        } catch(HttpClientErrorException httpClientErrorException) {
            // Wenn es fehlschlägt, in Abhängigkeit des HTTP-Status-Codes anders vorgehen
            // Wenn Resource nicht gefunden wird, dann null zurückgeben
            if(httpClientErrorException.getStatusCode() == HttpStatus.NOT_FOUND){
                return null;
            } else {
                // Ansonsten den Fehler weiter reichen
                throw httpClientErrorException;
            }
        }
    }
}
