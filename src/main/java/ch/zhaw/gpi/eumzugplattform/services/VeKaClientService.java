package ch.zhaw.gpi.eumzugplattform.services;

import ch.zhaw.gpi.eumzugplattform.processdata.VeKaResponse;
import java.util.Date;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;
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
     * Methode, um die Grundversicherung einer Person/Karte zu prüfen
     * 
     * @param baseInsuranceNumber   Versicherungskartennummer
     * @param firstName             Vorname
     * @param officialName          Nachname
     * @param dateOfBirth           Geburtsdatum
     * @return                      VeKaResponse mit zwei String-Variablen (Yes/No/Unknown und erläuternden Details)
     */
    public VeKaResponse checkBaseInsurance(Long baseInsuranceNumber, String firstName, String officialName, Date dateOfBirth) {
        // Die URL besteht ausschliesslich aus Strings, daher ist eine Übersetzung
        // des Geburtsdatums in einen passend formatierten String erforderlich
        DateFormatter dateFormatter = new DateFormatter("dd.MM.yyyy");
        String dateOfBirthString = dateFormatter.print(dateOfBirth, Locale.GERMAN);
        
        // Aufruf des REST-Services über die getForObject-Methode von RestTemplate, welche als Parameter erwartet:
        // - Die URL mit Parametern, wobei der in {} gesetzte Teil dann durch die Werte ersetzt wird
        // - Die Klassendefinition, in welcher die Antwort aufbereitet werden soll (von JSON zu dieser Klasse)
        // - Eine passende Anzahl an Parameter-Werten, welche die {} in der URL ersetzt
        // Zurücknehmen der Ressource und Deserialisierung als VeKaResponse-Objekt
        VeKaResponse veKaResponse = restTemplate.getForObject(
                vekaEndpoint + "/checkbaseinsurance?firstName={FIRSTNAME}&officialName={OFFICIALNAME}&dateOfBirth={DATEOFBIRTH}&baseInsuranceNumber={BASEINSURANCENUMBER}", 
                VeKaResponse.class, 
                firstName, 
                officialName, 
                dateOfBirthString, 
                Long.toString(baseInsuranceNumber));
        
        // Die erhaltene Antwort weiter reichen an die aufrufende Methode
        return veKaResponse;
    }
}
