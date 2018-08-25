package ch.zhaw.gpi.eumzugplattform.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service für die Umzugsplattform, welcher als Client mit Stripe kommuniziert
 * 
 * Kommuniziert indirekt über die Spring Java API-Library mit den Stripe-Servern
 * 
 * Durch @Service-Annotation wird beim Initialisieren der Umzugsplattform automatisch
 * eine Bean erzeugt
 * 
 * @author scep
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
     * @return              Object, welches bei erfolgreicher Belastung eine com.stripe.model.Charge-Instanz ist und bei Fehlschlagen eine com.stripe.exception.StripeException-Instanz 
     */
    public Object chargeCreditCard(String stripeToken, double amount) {
        try {
            // Die für die Anfrage erforderlichen Parameter in einer HashMap zusammenstellen
            Map<String, Object> chargeParams = new HashMap<>();
            // Betrag * 100, da Stripe Rappen-Beträge erwartet
            chargeParams.put("amount", (int) (amount * 100));
            chargeParams.put("currency", "CHF");
            // source = welches Zahlungsmittel belastet werden soll
            chargeParams.put("source", stripeToken);
            
            // Eine Belastungsanfrage erstellen
            Charge charge = Charge.create(chargeParams);
            
            // Falls es geklappt hat, dann das erhaltene Charge-Objekt zurückgeben
            return charge;
        } catch (StripeException e) {
            // Falls es nicht geklappt hat, dann das generische StripeException-Objekt gemäss // https://stripe.com/docs/api/java#error_handling auslesen und zurückgeben            
            return e;
        }
    }
}
