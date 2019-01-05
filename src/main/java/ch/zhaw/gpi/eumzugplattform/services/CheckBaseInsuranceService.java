package ch.zhaw.gpi.eumzugplattform.services;

import ch.zhaw.gpi.eumzugplattform.helpers.DateConversionHelper;
import ch.zhaw.gpi.eumzugplattform.processdata.PersonPD;
import ch.zhaw.gpi.eumzugplattform.processdata.VeKaCard;
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

    public void checkBaseInsuranceValidity(PersonPD person) {
        // Zur Kartennummer passende Karte suchen
        VeKaCard veKaCard = veKaClientService.getVeKaCard(person.getBaseInsuranceNumber());

        // Falls keine Karte gefunden wurde, dann entsprechende Antwort setzen und die weitere Ausführung abbrechen
        if (veKaCard == null) {
            person
                    .setCheckBaseInsuranceResult("Unknown")
                    .setCheckBaseInsuranceResultDetails("Keine Karte zu dieser Nummer gefunden. Mögliche Gründe: (Un)bewusste Falscheingabe oder der Kartenaussteller ist nicht bei der VeKa registriert.");
            return;
        }

        // Falls Karte abgelaufen ist, entsprechende Antwort setzen und die weitere Ausführung abbrechen
        if (veKaCard.getExpiryDate().before(new Date())) {
            person
                    .setCheckBaseInsuranceResult("No")
                    .setCheckBaseInsuranceResultDetails("Karte abgelaufen");
            return;
        }

        /**
         * Falls Personendaten zur Karte nicht mit übergebenen Personendaten
         * übereinstimmen, entsprechende Antwort setzen
         */
        // PersonPD aus der Karte auslesen
        PersonPD insuredPerson = veKaCard.getInsuredPerson();

        // Hilfsvariable, um die allenfalls abweichenden Typen von Personalien aufzunehmen
        String abweichendePersonalien = "";

        // Für Vergleich der Geburtsdaten müssen beide Daten ins gleiche Format gebracht werden (v.a. wegen unterschiedlicher Zeitzonen)
        // Hinweis: Gibt keinen Abzug, falls nicht implementiert, da mit den in soapUI übergebenen Testdaten dieses Problem nicht auftritt
        LocalDate paramDate = dateConversionHelper.convertToLocalDateViaInstant(person.getDateOfBirth());
        LocalDate vekaDate = dateConversionHelper.convertToLocalDateViaInstant(insuredPerson.getDateOfBirth());

        // Geburtsdatum auf Abweichung prüfen (compareTo gibt Abweichung der Tage als int zurück)
        if (paramDate.compareTo(vekaDate) != 0) {
            // Falls abweichend, dann die Hilfsvariable um den Typ der Personalie erweitern
            abweichendePersonalien += "Geburtsdatum";
        }

        // Vornamen auf Abweichung prüfen
        if (!insuredPerson.getFirstName().equals(person.getFirstName())) {
            // Falls abweichend, dann die Hilfsvariable um den Typ der Personalie erweitern
            abweichendePersonalien += (abweichendePersonalien.isEmpty() ? "" : ", ") + "Vorname";
        }

        // Nachname auf Abweichung prüfen
        if (!insuredPerson.getOfficialName().equals(person.getOfficialName())) {
            // Falls abweichend, dann die Hilfsvariable um den Typ der Personalie erweitern
            abweichendePersonalien += (abweichendePersonalien.isEmpty() ? "" : ", ") + "Nachname";
        }

        // Falls es Abweichungen gibt (also die Hilfsvariable nicht leer ist)
        if (!abweichendePersonalien.isEmpty()) {
            // Entsprechendes Resultat setzen und die weitere Ausführung abbrechen
            person
                    .setCheckBaseInsuranceResult("No")
                    .setCheckBaseInsuranceResultDetails("Karte gültig, aber Personalien nicht passend (" + abweichendePersonalien + ")");
            return;
        }

        // Falls Karte (Versicherung) keine Grundversicherung beinhaltet
        if (!veKaCard.isBaseInsured()) {
            // Entsprechendes Resultat setzen und die weitere Ausführung abbrechen
            person
                    .setCheckBaseInsuranceResult("No")
                    .setCheckBaseInsuranceResultDetails("Karte gültig, Personalien passend, aber nicht grundversichert");
            return;
        }

        // Falls alles in Ordnung ist, dann ein positives Resultat setzen
        person.setCheckBaseInsuranceResult("Yes");
    }
}
