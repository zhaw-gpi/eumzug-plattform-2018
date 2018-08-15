package ch.zhaw.gpi.eumzugplattform.helpers;

import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0058._5.SendingApplicationType;
import ch.zhaw.gpi.eumzugplattform.enumerations.WebServiceHeaderActionCodeEnumeration;
import java.util.Date;

/**
 * Initialisiert ein HeaderType-Objekt mit sinnvollen Standard-Eigenschaften
 * Hierdurch können mehrfach verwendete Eigenschaften wie z.B. die Bezeichnung
 * dieser Applikation einmalig gesetzt werden.
 */
public class WebServiceDefaultHeaderHelper {
    // Konstanten
    public static final String SENDER_ID = "sedex://umzugsplattform";
    public static final String SENDER_MANUFACTURER = "Kanton Bern";
    public static final String SENDER_PRODUCT = "Umzugsplattform";
    public static final String SENDER_VERSION = "1.0";
    public static final String MESSAGE_ACTION = WebServiceHeaderActionCodeEnumeration.NEW.code();
    public static final Boolean MESSAGE_TEST_FLAG = true;
    
    // Initialisieren des neuen HeaderType-Objekts
    private final HeaderType headerRequest = new HeaderType();
    
    // Konstruktor-Methode, welche den Header aus dem SOAP-Request als Parameter erwartet
    public WebServiceDefaultHeaderHelper() {        
        // Identifikation des Senders gemäss Sedex wird gesetzt (frei erfunden)
        headerRequest.setSenderId(SENDER_ID);
        
        // Ein neues Objekt mit Eigenschaften zur Umzugsplattform-Applikation wird erstellt
        SendingApplicationType sendingApplication = new SendingApplicationType();
        
        // Diesem Objekt wird der Hersteller/Betreiber zugewiesen
        sendingApplication.setManufacturer(SENDER_MANUFACTURER);
        
        // Diesem Objekt wird die Applikations-Bezeichnung zugewiesen
        sendingApplication.setProduct(SENDER_PRODUCT);
        
        // Diesem Objekt wird die Applikations-Version zugewiesen
        sendingApplication.setProductVersion(SENDER_VERSION);
        
        // Dieses Objekt wird dem Antwort-Header zugewiesen
        headerRequest.setSendingApplication(sendingApplication);
        
        // Ein neues Datumskonversation-Hilfsobjekt wird initialisiert
        DateConversionHelper dateConversionHelper = new DateConversionHelper();
        
        // Mithilfe dieses Hilfsobjekt wird das aktuelle Datum (inkl. Uhrzeit)
        // in ein XMLGregorianCalendar-Objekt umgewandelt und der Nachrichten-Datums-
        // Eigenschaft im Antwort-Header zugewiesen
        headerRequest.setMessageDate(DateConversionHelper.DateToXMLGregorianCalendar(new Date()));
        
        // Es wird angegeben, dass es sich um eine Testnachricht handelt (Entwicklungs-Modus)
        headerRequest.setTestDeliveryFlag(MESSAGE_TEST_FLAG);
        
        // Der Aktionscode nach eCH-0058 Seite 11 wird auf 1 (= neue Nachricht) gesetzt, theoretisch wäre vermutlich auch "5" (= Anfrage) korrekt!?
        headerRequest.setAction(MESSAGE_ACTION);
    }

    // Getter-Methode, um den Antwort-Header mit den Default-Eigenschaften zu erhalten
    public HeaderType getHeaderRequest() {
        return headerRequest;
    }
}
