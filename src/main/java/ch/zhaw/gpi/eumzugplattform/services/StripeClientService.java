package ch.zhaw.gpi.eumzugplattform.services;

import com.stripe.Stripe;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author scep
 */
@Service
public class StripeClientService {

    // Konstruktor
    @Autowired
    StripeClientService(
            @Value("${stripe.apiKey}") String stripeApiKey
    ) {
        // https://stripe.com/docs/api/java#authentication
        Stripe.apiKey = stripeApiKey;
    }

    public Object chargeCreditCard(String stripeToken, double amount) {
        try {
            // https://stripe.com/docs/api/java#charges respektive https://stripe.com/docs/api/java#create_charge
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", (int) (amount * 100));
            chargeParams.put("currency", "CHF");
            chargeParams.put("source", stripeToken);
            Charge charge = Charge.create(chargeParams);
            return charge;
            // https://stripe.com/docs/api/java#error_handling
        } catch (StripeException e) {
            
            return e;
        }
    }
}
