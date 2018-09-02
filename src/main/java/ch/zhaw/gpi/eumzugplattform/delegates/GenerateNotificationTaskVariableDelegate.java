package ch.zhaw.gpi.eumzugplattform.delegates;

import java.util.HashMap;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

/**
 * Erstellen der Notification-Aufgabe für den kantonalen Benachrichtigungsdienst
 * 
 * Der kantonale Benachrichtigungsdienst erwartet eine HashMap mit dort spezifierten 
 * Key-Value-Paaren. Diese HashMap wird in dieser Klasse durchgeführt.
 * 
 * @author scep
 */
@Named("GenerateNotificationTaskVariableAdapter")
public class GenerateNotificationTaskVariableDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution de) throws Exception {
        // Subjekt auslesen
        String subject = (String) de.getVariable("notificationSubject");
        
        // Nachricht auslesen
        String messageBody = (String) de.getVariable("notificationMessageBody");
        
        // Personalien und Kontaktangaben auslesen
        String officialName = (String) de.getVariable("officialName");
        String emailAddress = (String) de.getVariable("emailAddress");
        String phoneNumber = (String) de.getVariable("phoneNumber");
        Boolean enableSmsNotification = (Boolean) de.getVariable("enableSmsNotification");
        
        // Ein leeres NotificationTask-Objekt erstellen
        HashMap<String, String> notificationTask = new HashMap<>();
        
        // Vollständige Message aufbauen
        String message = "Sehr geehrter Herr " + officialName + "\n\n"
                + messageBody + "\n\nFreundliche Grüsse\nIhre Umzugsplattform";
        
        // notificationTask-Eigenschaften festlegen gemäss Spezifikation des Kantonalen Benachrichtigungsdienstes
        notificationTask.put("channels", "email" + (enableSmsNotification ? ", sms" : ""));
        notificationTask.put("emailMessage", message);
        notificationTask.put("emailFrom", "umzugsplattform@be.ch");
        notificationTask.put("emailSubject", subject);
        notificationTask.put("emailTo", emailAddress);
        if(enableSmsNotification){
            notificationTask.put("smsMessage", messageBody);
            notificationTask.put("smsTo", phoneNumber);
        }
        
        // Notification-Task als Prozessvariable speichern
        de.setVariable("notificationTask", notificationTask);
    }
    
}
