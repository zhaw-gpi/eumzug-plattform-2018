package ch.zhaw.gpi.eumzugplattform.services;

import ch.zhaw.gpi.eumzugplattform.helpers.DateConversionHelper;
import ch.zhaw.gpi.eumzugplattform.processdata.CheckBaseInsuranceResult;
import ch.zhaw.gpi.eumzugplattform.processdata.VeKaCard;
import ch.zhaw.gpi.eumzugplattform.processdata.VeKaPerson;
import java.time.LocalDate;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Klasse, welche prüft, ob eine Person grundversichert ist
 * 
 * @author scep
 */
@Service
public class CheckBaseInsuranceService {
    
    // Verdrahten des VeKaClientServices
    @Autowired
    private VeKaClientService veKaClientService;
    
    // Verdrahten des DateConversionHelpers
    @Autowired
    private DateConversionHelper dateConversionHelper;
    
    /**
     * Methode, welche prüft, ob eine Person grundversichert ist, indirekt über
     * die Kartennummer, deren Vorhandensein und Gültigkeit und über die
     * Übereinstimmung der Personalien
     * 
     * @param baseInsuranceNumber   Grundversicherungskartennummer
     * @param firstName             Vorname
     * @param officialName          Nachname
     * @param dateOfBirth           Geburtsdatum
     * @return                      Objekt der CheckBaseInsuranceResult-Klasse
     */
    public CheckBaseInsuranceResult checkBaseInsuranceValidity(Long baseInsuranceNumber, String firstName, String officialName, Date dateOfBirth){
        // Result-Objekt instanzieren
        CheckBaseInsuranceResult checkBaseInsuranceResult = new CheckBaseInsuranceResult();
        
        // Zur Kartennummer passende Karte suchen
        VeKaCard veKaCard = veKaClientService.getVeKaCard(baseInsuranceNumber);
        
        // Falls keine Karte gefunden wurde, dann entsprechende Antwort zurück geben
        if(veKaCard == null) {
            checkBaseInsuranceResult
                    .setCheckResult("Unknown")
                    .setCheckResultDetails("Keine Karte zu dieser Nummer gefunden. Mögliche Gründe: (Un)bewusste Falscheingabe oder der Kartenaussteller ist nicht bei der VeKa registriert.");
            return checkBaseInsuranceResult;
        }
        
        // Falls Karte abgelaufen ist, entsprechende Antwort zurück geben
        if(veKaCard.getExpiryDate().before(new Date())){
            checkBaseInsuranceResult
                    .setCheckResult("No")
                    .setCheckResultDetails("Karte abgelaufen");
            return checkBaseInsuranceResult;
        }
        
        /**
         * Falls Personendaten zur Karte nicht mit übergebenen Personendaten 
         * übereinstimmen, entsprechende Antwort zurück geben
         */
        // Person aus der Karte auslesen
        VeKaPerson insuredPerson = veKaCard.getInsuredPerson();
        
        // Hilfsvariable, um die allenfalls abweichenden Typen von Personalien aufzunehmen
        String abweichendePersonalien = "";
        
        // Für Vergleich der Geburtsdaten müssen beide Daten ins gleiche Format gebracht werden (v.a. wegen unterschiedlicher Zeitzonen)
        LocalDate paramDate = dateConversionHelper.convertToLocalDateViaInstant(dateOfBirth);
        LocalDate vekaDate = dateConversionHelper.convertToLocalDateViaInstant(insuredPerson.getDateOfBirth());
        
        // Geburtsdatum auf Abweichung prüfen (compareTo gibt Abweichung der Tage als int zurück)
        if(paramDate.compareTo(vekaDate) != 0){
            // Falls abweichend, dann die Hilfsvariable um den Typ der Personalie erweitern
            abweichendePersonalien += "Geburtsdatum";
        }
        
        // Vornamen auf Abweichung prüfen
        if(!insuredPerson.getFirstName().equals(firstName)){
            // Falls abweichend, dann die Hilfsvariable um den Typ der Personalie erweitern
            abweichendePersonalien += (abweichendePersonalien.isEmpty() ? "" : ", ") + "Vorname";
        }
        
        // Nachname auf Abweichung prüfen
        if(!insuredPerson.getOfficialName().equals(officialName)){
            // Falls abweichend, dann die Hilfsvariable um den Typ der Personalie erweitern
            abweichendePersonalien += (abweichendePersonalien.isEmpty() ? "" : ", ") + "Nachname";
        }
        
        // Falls es Abweichungen gibt (also die Hilfsvariable nicht leer ist)
        if(!abweichendePersonalien.isEmpty()){
            // Entsprechendes Resultat zurückgeben
            checkBaseInsuranceResult
                    .setCheckResult("No")
                    .setCheckResultDetails("Karte gültig, aber Personalien nicht passend (" + abweichendePersonalien + ")");
            return checkBaseInsuranceResult;
        }
        
        // Falls Karte (Versicherung) keine Grundversicherung beinhaltet
        if(!veKaCard.isBaseInsured()){
            // Entsprechendes Resultat zurückgeben
            checkBaseInsuranceResult
                    .setCheckResult("No")
                    .setCheckResultDetails("Karte gültig, Personalien passend, aber nicht grundversichert");
            return checkBaseInsuranceResult;
        }
        
        // Falls alles in Ordnung ist, dann ein positives Resultat zurück geben
        return checkBaseInsuranceResult.setCheckResult("Yes");
    }
    
    
}
