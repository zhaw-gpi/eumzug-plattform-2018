package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.services.StripeClientService;
import com.stripe.exception.StripeException;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Zahlungsanfrage an den StripeClientService senden und synchrone Antwort auswerten
 * 
 * Es wird eine Kartenbelastungsanfrage an den StripeClientService mit dem Token und 
 * dem zu bezahlenden Betrag aus den Prozessvariablen übergeben.
 * 
 * Synchron kommt von diesem Service eine Antwort zurück, entweder eine erfolgreiche 
 * Belastung der Kreditkarte (Charge) oder ein Error (StripeException). Im ersten Fall 
 * passiert in dieser Delegate-Klasse nichts mehr, im anderen Fall wird ein BPMN-Fehler geworfen.
 * 
 * @author scep
 */
@Named("createChargeAdapter")
public class CreateChargeDelegate implements JavaDelegate {
    
    // Verdrahten des StripeClientServices in einer lokalen Variable
    @Autowired
    private StripeClientService stripeClientService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        // Aus den Prozessvariablen das Stripe-Token und den zu bezahlenden Betrag auslesen
        String stripeToken = (String) delegateExecution.getVariable("stripeToken");
        Integer feesTotal = (Integer) delegateExecution.getVariable("feesTotal");
        
        // Die Kartenbelastungsanfrage an den Stripe Client Service stellen und das
        // Ergebnis in einem Object speichern
        Object chargeReturn = stripeClientService.chargeCreditCard(stripeToken, feesTotal);
        
        // Das Ergebnis ist entweder ein Charge-Objekt oder eine StripeException
        // Im ersten Fall ist nichts zu tun, im zweiten Fall ...
        if(chargeReturn instanceof StripeException){
            // .. ist die Fehlermeldung auszulesen ...
            String errorMessage = ((StripeException) chargeReturn).getMessage();
            // ... und ein neuer BPMN-Fehler mit dem Namen ErrorKartenbelastungFehlgeschlagen und der Fehlermeldung als Nachricht  an die ProcessEngine zu werfen
            throw new BpmnError("ErrorKartenbelastungFehlgeschlagen", errorMessage);
        }
    }
}
