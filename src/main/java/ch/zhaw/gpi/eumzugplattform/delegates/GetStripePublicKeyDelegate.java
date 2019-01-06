package ch.zhaw.gpi.eumzugplattform.delegates;

import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;

/**
 * Wandelt die Umgebungsvariable mit dem öffentlichen Stripe API-Key in eine 
 * Prozessvariable stripePublicKey um, damit diese im 
 * ErfassenDerZahlungsdetails-Formular genutzt werden kann.
 * 
 * @author scep
 */
@Named("getStripePublicKeyAdapter")
public class GetStripePublicKeyDelegate implements JavaDelegate {
    
    private final String stripePublicKey;
    
    GetStripePublicKeyDelegate(@Value("${stripe.apiPublicKey}") String key) {
        stripePublicKey = key;
    }

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {    
        delegateExecution.setVariable("stripePublicKey", stripePublicKey);
    }
    
}
