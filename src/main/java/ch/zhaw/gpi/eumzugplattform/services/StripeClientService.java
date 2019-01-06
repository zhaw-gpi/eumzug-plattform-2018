package ch.zhaw.gpi.eumzugplattform.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service für die Umzugsplattform, welcher als Client mit Stripe kommuniziert
 * 
 * Kommuniziert indirekt über die Spring Java API-Library mit den Stripe-Servern
 * 
 * Enthält Methode, um eine Kartenbelastung vorzunehmen. Enthält Methode, um die Zahlungseingänge innerhalb eines Zeitraums zu erhalten.
 * 
 * Durch @Service-Annotation wird beim Initialisieren der Umzugsplattform automatisch
 * eine Bean erzeugt
 * 
 * @author Stefan und scep
 */
@Service
public class StripeClientService {

    
    /**
     * Konstruktur-Methode, welche beim Erstellen der Bean den Secret API Key
     * für die Kommunikation mit der Stripe API aus einer in application.properties
     * gesetzten Eigenschaft ausliest und dem Stripe-Objekt übergibt
     * 
     * @param stripeApiKey 
     */
    StripeClientService(@Value("${stripe.apiKey}") String stripeApiKey) {
        // https://stripe.com/docs/api/java#authentication
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Sendet eine Krediktartenbelastungsanfrage an Stripe und gibt das erhaltene Ergebnis zurück
     * 
     * Gemäss den Anleitungen in https://stripe.com/docs/api/java#charges respektive https://stripe.com/docs/api/java#create_charge
     * 
     * @param stripeToken   Token, welches indirekt die Zahlungsdetails enthält 
     * @param amount        Der zu belastende Betrag in Franken
     * @param feeMap        Gebührenbeschreibung und -wert als Key-Value-Paar
     * @return              Object, welches bei erfolgreicher Belastung eine com.stripe.model.Charge-Instanz ist und bei Fehlschlagen eine com.stripe.exception.StripeException-Instanz 
     */
    public Object chargeCreditCard(String stripeToken, double amount, HashMap<String, Integer> feeMap) {
        try {
            // Die für die Anfrage erforderlichen Parameter in einer HashMap zusammenstellen
            Map<String, Object> chargeParams = new HashMap<>();
            
            // Betrag * 100, da Stripe Rappen-Beträge erwartet
            chargeParams.put("amount", (int) (amount * 100));
            
            // Währung Schweizer Franken
            chargeParams.put("currency", "CHF");
            
            // Gebühren-Map als Metadaten
            chargeParams.put("metadata", feeMap);
            
            // source = welches Zahlungsmittel belastet werden soll, indirekt referenziert über das beschränkte Zeit gültige Token
            chargeParams.put("source", stripeToken);
            
            // Eine Belastungsanfrage erstellen und die Antwort zurücknehmen in ein Charge-Objekt
            Charge charge = Charge.create(chargeParams);
            
            // Falls es geklappt hat, dann das erhaltene Charge-Objekt zurückgeben
            return charge;
        } catch (StripeException e) {
            // Falls es nicht geklappt hat, dann das generische StripeException-Objekt gemäss // https://stripe.com/docs/api/java#error_handling zurückgeben            
            return e;
        }
    }
    
    /**
     * Methode zum Generieren einer Liste mit allen Zahlungen eines Monats
     * 
     * @param startDate Startdatum des Monats als UnixDate
     * @param endDate   Enddatum des Monats als UnixDate
     * @return          ChargeCollection bei Erfolg, StripeException bei Fehler
     * @throws com.stripe.exception.StripeException
     */
    public List<Charge> getPaymentList(long startDate, long endDate) throws StripeException {
        
        // Leere Map für Parameter der Anfrage an Stripe erstellen
        Map<String, Object> chargeCollectionParams = new HashMap<>();
        
        // Parameter setzen als Filter mit Start- und Enddatum (gte=greater or equal, lte=less or equal)
        chargeCollectionParams.put("created[gte]", startDate);
        chargeCollectionParams.put("created[lte]", endDate);
        // chargeParams.put("limit", 100);
        
        // Anfrage für eine Liste von Zahlungen mit den Parametern ausführen
        ChargeCollection chargeCollection = Charge.list(chargeCollectionParams);
        
        // Durch die Liste iterieren, um nicht erfolgreiche aus der Liste zu löschen
        for (Iterator<Charge> iterator = chargeCollection.getData().iterator(); iterator.hasNext();) {
            Charge charge = iterator.next();
            // Falls Status nicht erfolgreich ...
            if (!"succeeded".equals(charge.getStatus())){
                // ... dann das Element entfernen
                iterator.remove();
        }}

        // Bereinigte Liste zurückgeben
        return chargeCollection.getData();
    }
}
