package ch.zhaw.gpi.eumzugplattform.webserviceclients;

import ch.zhaw.iwi.gpi.gwr.AddresseExistenzType;
import ch.zhaw.iwi.gpi.gwr.AdressPruefungOperation;
import ch.zhaw.iwi.gpi.gwr.AdressPruefungOperationResponse;
import ch.zhaw.iwi.gpi.gwr.AdresseType;
import ch.zhaw.iwi.gpi.gwr.WohnungenAntwortType;
import ch.zhaw.iwi.gpi.gwr.WohnungenInGebaeudeOperation;
import ch.zhaw.iwi.gpi.gwr.WohnungenInGebaeudeOperationResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Web Service Client für Wegzugsadress-Eingaben auf Übereinstimmung in Gebäude- und Wohnungsregister prüfen
 * 
 * Diese Klasse wird aufgerufen von CheckAdressMoveOutDelegate, welche der Methode adressPruefungOperation ein AdresseType-Objekt übergibt, welches die Adress-Angaben enthält.
 * 
 * Diese Klasse ist zuständig, um die Anfrage und Antwort an den Web Service in der gewünschten Form aufzubereiten/auszuwerten. Die eigentliche Kommunikation läuft dann im Hintergrund über die Web Service Template-Bean, welche in GenericWebServiceClientConfiguration definiert ist. Die Operation adressPruefungOperation des GWR-Web Service (http://localhost:8090/soap/GebaeudeUndWohnungsRegisterService) wird dabei synchron aufgerufen.
 * 
 * @author scep
 */
@Component
public class GwrWebServiceClient {
    // Konstanten
    public final static QName ADRESS_PRUEFUNG_OPERATION_QNAME = new QName("http://www.iwi.zhaw.ch/gpi/gwr", "adressPruefungOperation");
    public final static QName WOHNUNGEN_IN_GEBAEUDE_OPERATION_QNAME = new QName("http://www.iwi.zhaw.ch/gpi/gwr", "wohnungenInGebaeudeOperation");
    
    // Werte aus application.properties
    @Value("${gwr.endpoint}")
    private String webserviceUrl;

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    /**
     * Diese Methode prüft mit einer Adresse (Typ: AdresseType) ob diese Im GWR
     * vorhanden ist oder nicht. Sofern die Response vom Webservice vom Typ
     * AdressPruefungOperationResponse ist, wird ein Objekt vom Typ
     * AdresseExistenzType zurückgegeben. Ist es nicht von diesem Typ, so wird
     * eine Exception geworfen.
     *
     * @param adresse
     * @return
     */
    public AddresseExistenzType adressPruefungOperation(AdresseType adresse) {
        AdressPruefungOperation adressPruefungOperationRequest = new AdressPruefungOperation();
        adressPruefungOperationRequest.setAdresspruefungAnfrage(adresse);

        Object webServiceResponse = webServiceTemplate.marshalSendAndReceive(webserviceUrl,
                new JAXBElement(ADRESS_PRUEFUNG_OPERATION_QNAME, AdressPruefungOperation.class, adressPruefungOperationRequest));
 
        Object jAXBElement = ((JAXBElement) webServiceResponse).getValue();
        if (jAXBElement instanceof AdressPruefungOperationResponse) {
            AdressPruefungOperationResponse adressPruefungOperationResponse = (AdressPruefungOperationResponse) jAXBElement;
            AddresseExistenzType addresseExistenz = adressPruefungOperationResponse.getAdresspruefungAntwort();
            return addresseExistenz;
        } else {
            throw new UnsupportedOperationException("Webservice reutrned unsupported type: " + jAXBElement.getClass().getName());
        }
    }
    
    /**
     * Diese Methode prüft mit einer Adresse (Typ: AdresseType) ob Wohnungen im
     * GWR vorhanden sind oder nicht. Sofern die Response vom Webservice vom Typ
     * WohnungenInGebaeudeOperationResponse ist, wird ein Objekt vom Typ
     * WohnungenAntwortType zurückgegeben. Ist es nicht von diesem Typ, so wird
     * eine Exception geworfen.
     *
     * @param adresse
     * @return
     */
    public WohnungenAntwortType wohnungenAnfrage(AdresseType adresse) {
        // Neues Objekt WohnungenInGebaeudeOperation instanzieren und die mitgegebene
        // adresse vom Typ AdresseType setzen
        WohnungenInGebaeudeOperation wohnungenInGebaeudeOperation = new WohnungenInGebaeudeOperation();
        wohnungenInGebaeudeOperation.setWohnungenAnfrage(adresse);

        // Webservice-Aufruf mit der Ziel-URL http://localhost:8090/soap/GebaeudeUndWohnungsRegisterService,
        // dem QName und dem Objekt wohnungenInGebaeudeOperation
        Object webServiceResponse = webServiceTemplate.marshalSendAndReceive(webserviceUrl,
                new JAXBElement(WOHNUNGEN_IN_GEBAEUDE_OPERATION_QNAME, WohnungenInGebaeudeOperation.class, wohnungenInGebaeudeOperation));

        // Antwort zu einem jAXBElement casten
        Object jAXBElement = ((JAXBElement) webServiceResponse).getValue();

        // Überprüfen ob es vom korrekten Typ ist ...
        if (jAXBElement instanceof WohnungenInGebaeudeOperationResponse) {
            // ... Response ist vom korrekten Typ

            // Response speichern und zurückgeben
            WohnungenInGebaeudeOperationResponse wohnungenInGebaeudeOperationResponse = (WohnungenInGebaeudeOperationResponse) jAXBElement;
            WohnungenAntwortType wohnungen = wohnungenInGebaeudeOperationResponse.getWohnungenAntwort();
            return wohnungen;
        } else {
            // ... Response ist nicht vom korrekten Typ

            // Exception werfen
            throw new UnsupportedOperationException("Webservcice returned unsupported type: " + jAXBElement.getClass().getName());
        }
    }
}
