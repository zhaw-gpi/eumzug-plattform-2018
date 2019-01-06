package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.helpers.DateConversionHelper;
import ch.zhaw.gpi.eumzugplattform.services.StripeClientService;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Die DelegateKlasse ruft den StripeClientService mit einer Methode getPaymentList auf. 
 * 
 * Die Liste wird auf erfolgreiche Meldungen geprüft und ein "Bericht" (einfache Text-Liste) erstellt.
 * 
 * @author Stefan (TZc01), TZc05 (Datumsbestimmung) und scep
 */
@Named("preparePaymentReportAdapter")
public class PreparePaymentReportDelegate implements JavaDelegate {

    // Verdrahten des StripeClientService für das Aufbereiten der Liste von Zahlungen
    @Autowired
    StripeClientService stripeClientService;
    
    // Verdrahten des DateConversionHelpers für UnixDate Start/Ende Monat
    @Autowired
    private DateConversionHelper dateConversionHelper;
    
    // Sender-Adresse aus entsprechender Property-Eigenschaft lesen
    @Value("${umzugsplattform.mailaddress}")
    private String fromAddress;
    
    // Empfänger-Adresse aus entsprechender Property-Eigenschaft lesen
    @Value("${paymentreport.receiver}")
    private String toAddress;
    
    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        
        // *** START- UND ENDDATUM IN MILLISEKUNDEN BESTIMMEN
        // theoretisch müsste noch die Zeitzone des Stripe-Accounts genutzt werden
        // Kalender lastMonth initialisieren
        Calendar previousMonth = Calendar.getInstance();
        
        // Kalender ein Monat vom jetzigen Datum zurückgehen 
        // Für Testzwecke anstelle -1 die Zahl 0 nehmen
        previousMonth.add(Calendar.MONTH, -1);
        
        // Ersten Tag (1) sowie alle Zeit-Felder auf 0setzen und der Variable firstDateOfPreviousMonth zuweisen
        previousMonth.set(Calendar.DATE, 1);
        previousMonth.set(Calendar.HOUR, 0);
        previousMonth.set(Calendar.MINUTE, 0);
        previousMonth.set(Calendar.SECOND, 0);
        previousMonth.set(Calendar.MILLISECOND, 0);
        Long firstDateOfPreviousMonth = previousMonth.getTimeInMillis() / 1000;

        // Monat als Zeichenfolge auslesen (z.B. "Januar")
        String monthName = previousMonth.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, Locale.GERMAN);
        
        // Letzten Tag (23:59:59.999) setzen und der Variable lastDateOfPreviousMonth zuweisen
        previousMonth.set(Calendar.DATE, previousMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
        previousMonth.set(Calendar.HOUR, 23);
        previousMonth.set(Calendar.MINUTE, 59);
        previousMonth.set(Calendar.SECOND, 59);
        previousMonth.set(Calendar.MILLISECOND, 999);
        Long lastDateOfPreviousMonth = previousMonth.getTimeInMillis() / 1000;
        
        
        
        
        // *** MAIL-PARAMETER VORBEREITEN
        // Eine leere HashMap erstellen, welche die für den Versand benötigten Elemente beinhaltet
        HashMap<String, String> reportElements = new HashMap<>();
        
        // Grundelemente setzen (Kanal und Sender-Adresse)
        reportElements.put("channels", "email");
        reportElements.put("emailFrom", fromAddress);
        reportElements.put("emailTo", toAddress);
        
        // *** STRIPE CLIENT SERVICE-METHODE AUFRUFEN UND BERICHT AUFBEREITEN
        try{
            // Stripe Client Service-Methode aufrufen 
            List<Charge> charges = stripeClientService.getPaymentList(firstDateOfPreviousMonth, lastDateOfPreviousMonth);
            
            // Subjekt des Mails setzen
            reportElements.put("emailSubject", "Zahlungsbericht für Monat " + monthName);
            
            // Hauptinhalt des Mails wird über einen StringBuilder erstellt, der hier instanziert wird mit Eingangszeile und zwei Zeilenumbrüchen
            StringBuilder mailBody = new StringBuilder("Die folgenden Zahlungen sind im vergangenen Monat eingegangen:");
            mailBody.append(System.getProperty("line.separator"));
            mailBody.append(System.getProperty("line.separator"));
            
            // Wenn die Liste keine Einträge enthält
            if(charges.isEmpty()){
                // Entsprechende Meldung einfügen
                mailBody.append("Keine erfolgreichen Zahlungseingänge im vorhergehenden Monat");
            } else {
                // Über die Zahlungseinträge iterieren
                for(Charge charge : charges){
                    // ** Datum in lesbarem Format aufbereiten
                    // Erstelldatum herauslesen und mit 1000 multiplizieren wegen UnixDate in Sekunden
                    Date date = new Date(charge.getCreated()*1000L);

                    // Datum formatieren
                    SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd-MM-yyyy");
                    String dateFormatted = simpleDateFormat.format(date);

                    // ** Betrag in lesbarem Format aufbereiten
                    NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("de_CH"));
                    String amountFormatted = numberFormat.format(charge.getAmount() / 100);
                    
                    // ** Beschreibung aufbereiten
                    // Liste von Einzelbeschreibungen instanzieren
                    List<String> singleEntries = new ArrayList<>();
                    
                    // Über jeden Eintrag in der Metadaten-Map (ursprünglich feeMap) iterieren
                    for(Map.Entry<String, String> keyValuePair : charge.getMetadata().entrySet()){
                        // Der Liste von Einzelbschreibungen eine zusammengesetzte Zeichenfolge übergeben
                        singleEntries.add(keyValuePair.getKey() + " (" + keyValuePair.getValue() + ")");
                    }
                    
                    // Falls Liste der Beschreibungs-Einträge nicht leer ist ...
                    String descriptionFormatted;
                    if(!singleEntries.isEmpty()){
                        // Eine Beschreibung zusammenbauen aus den Einzelbeschreibungen
                        descriptionFormatted = String.join(", ", singleEntries);
                    } else {
                        // Prüfen, ob eine sonstige Beschreibung vorhanden ist
                        if(charge.getDescription() != null && charge.getDescription().isEmpty()){
                            // Falls ja, diese übernehmen
                            descriptionFormatted = charge.getDescription();
                        } else {
                            // Falls nicht, Standardbeschreibung erstellen
                            descriptionFormatted = "Keine Beschreibung vorhanden";
                        }                        
                    }                   
                    

                    // *** Eine Zahlungseingangs-Zeile zusammenbauen
                    // Datum
                    mailBody.append(dateFormatted);

                    // Doppelpunkt und Leerzeichen
                    mailBody.append(": ");

                    // Betrag
                    mailBody.append(amountFormatted);

                    // Komma und Leerzeichen
                    mailBody.append(" - ");

                    // Beschreibung
                    mailBody.append(descriptionFormatted);

                    // Neue Zeile
                    mailBody.append(System.getProperty("line.separator"));
                }
            }
            
            // Abschlusszeilen hinzufügen
            mailBody.append(System.getProperty("line.separator"));
            mailBody.append("Freundliche Grüsse");
            mailBody.append(System.getProperty("line.separator"));
            mailBody.append("Ihre Umzugsplattform");
            
            // Den Hauptteil der HashMap hinzufügen
            reportElements.put("emailMessage", mailBody.toString());
        } catch(StripeException stripeException){
            // Falls ein Fehler auftritt, soll eine Fehlermeldung per Mail aufbereitet werden
            reportElements.put("emailSubject", "Zahlungsbericht für Monat " + monthName + ": Fehlgeschlagen");
            reportElements.put("emailMessage", "Fehler aufgetreten bei Stripe. Details: " + stripeException.getLocalizedMessage());
        }
        
        // HashMap in Prozessvariable speichern
        delegateExecution.setVariable("sendPaymentReportTaskVariable", reportElements);
    }
}
