package ch.zhaw.gpi.eumzugplattform.services;

import ch.zhaw.gpi.eumzugplattform.processdata.Person;
import ch.zhaw.gpi.eumzugplattform.processdata.PersonList;
import ch.zhaw.gpi.eumzugplattform.processdata.VeKaAddress;
import ch.zhaw.gpi.eumzugplattform.processdata.VeKaCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Klasse, welche die neue Adresse für alle mitumziehenden Personen dem VeKa-Dienst mitteilt
 * sofern die Grundversicherungsprüfung erfolgreich war
 * 
 * @author scep
 */
@Service
public class UpdateAddressAtVekaService {
    
    // Verdrahten des VeKaClientServices
    @Autowired
    private VeKaClientService veKaClientService;
    
    public void updateAddressForPersons(PersonList personList, String street, String houseNumber, Integer plz, String town){
        // Ein neues Adress-Objekt instanzieren
        VeKaAddress address = new VeKaAddress();
        
        // Diesem die Parameter-Werte zuweisen
        address
                .setStreet(street)
                .setHouseNumber(houseNumber)
                .setPlz(plz)
                .setTown(town);
        
        // Über alle Personen iterieren
        for(Person person : personList.getPersons()){
            // Falls die Person grundversichert ist,
            if(person.getCheckBaseInsuranceResult().equals("Yes")){
                // Zur Kartennummer passende Karte suchen
                VeKaCard veKaCard = veKaClientService.getVeKaCard(person.getBaseInsuranceNumber());
                
                // Zur Sicherheit prüfen, ob eine zurück erhalten wurde, auch wenn sehr unwahrscheinlich, dass nicht
                if(veKaCard != null) {
                    // Die VeKa-Personen-Id (neues Attribut in Person) auslesen
                    Long vekaPersonId = veKaCard.getInsuredPerson().getId();
                    
                    // Addressänderung an VeKa-Client-Service übergeben (der Einfachheit halber ohne Fehlerprüfung)
                    veKaClientService.updateAddressForPerson(vekaPersonId, address);
                }
            }
        }
    }
}