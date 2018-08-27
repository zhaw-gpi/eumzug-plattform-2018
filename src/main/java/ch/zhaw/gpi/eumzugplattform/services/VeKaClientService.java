package ch.zhaw.gpi.eumzugplattform.services;

import ch.zhaw.gpi.eumzugplattform.processdata.VeKaResponse;
import java.util.Date;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author scep
 */
@Component
public class VeKaClientService {
    
    private final RestTemplate restTemplate;
    
    @Value("${veka.endpoint}")
    private String vekaEndpoint;

    public VeKaClientService() {
        restTemplate = new RestTemplate();
    }
    
    public VeKaResponse checkBaseInsurance(Long baseInsuranceNumber, String firstName, String officialName, Date dateOfBirth) {
        
        DateFormatter dateFormatter = new DateFormatter("dd.MM.yyyy");
        String dateOfBirthString = dateFormatter.print(dateOfBirth, Locale.GERMAN);
        VeKaResponse veKaResponse = restTemplate.getForObject(
                vekaEndpoint + "/checkbaseinsurance?firstName={FIRSTNAME}&officialName={OFFICIALNAME}&dateOfBirth={DATEOFBIRTH}&baseInsuranceNumber={BASEINSURANCENUMBER}", 
                VeKaResponse.class, 
                firstName, 
                officialName, 
                dateOfBirthString, 
                Long.toString(baseInsuranceNumber));
        
        return veKaResponse;
    }
}
