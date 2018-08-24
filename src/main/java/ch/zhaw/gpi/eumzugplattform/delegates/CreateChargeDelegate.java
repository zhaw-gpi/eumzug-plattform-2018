package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.services.StripeClientService;
import com.stripe.exception.StripeException;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author scep
 */
@Named("createChargeAdapter")
public class CreateChargeDelegate implements JavaDelegate {
    
    @Autowired
    private StripeClientService stripeClientService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        String stripeToken = (String) delegateExecution.getVariable("stripeToken");
        Integer feesTotal = (Integer) delegateExecution.getVariable("feesTotal");
        Object chargeReturn = stripeClientService.chargeCreditCard(stripeToken, feesTotal);
        if(chargeReturn instanceof StripeException){
            String errorMessage = ((StripeException) chargeReturn).getMessage();
            throw new BpmnError("ErrorKartenbelastungFehlgeschlagen", errorMessage);
        }
    }
    
}
