package ch.zhaw.gpi.eumzugplattform.webserviceclients;

import ch.ech.xmlns.ech_0058._5.HeaderType;
import ch.ech.xmlns.ech_0194._1.DeliveryType;
import ch.ech.xmlns.ech_0194._1.HandleDelivery;
import ch.ech.xmlns.ech_0194._1.HandleDeliveryResponse;
import ch.ech.xmlns.ech_0194._1.InfoType;
import ch.ech.xmlns.ech_0194._1.NegativeReportType;
import ch.ech.xmlns.ech_0194._1.PersonMoveRequest;
import ch.ech.xmlns.ech_0194._1.PersonMoveResponse;
import ch.zhaw.gpi.eumzugplattform.enumerations.WebServiceHeaderActionCodeEnumeration;
import ch.zhaw.gpi.eumzugplattform.helpers.WebServiceDefaultHeaderHelper;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Web Service Client für Personidentifikation im kantonalen Personenregister
 * 
 * Diese Klasse wird aufgerufen von IdentifyPersonDelegate, welche der Methode identifyPerson ein personMoveRequest-Objekt übergibt, welches die Identifiktationsmerkmale der anzugfragenden Person enthält.
 * 
 * Diese Klasse ist zuständig, um die Anfrage und Antwort an den Web Service in der gewünschten Form aufzubereiten/auszuwerten, gemeinsam mit den Hilfsklassen WebServiceHeaderActionCodeEnumeration und WebServiceDefaultHeaderHelper. Die eigentliche Kommunikation läuft dann im Hintergrund über die Web Service Template-Bean, welche in GenericWebServiceClientConfiguration definiert ist. Die Operation identifyPerson des Personenregister-Web Service (http://localhost:8083/soap/PersonenRegisterService) wird dabei synchron aufgerufen.
 * 
 * @author scep
 */
@Component
public class ResidentRegisterWebServiceClient {

    @Autowired
    private WebServiceTemplate webServiceTemplate;
    
    // Konstanten
    public final static QName HANDLE_DELIVERY_QNAME = new QName("http://www.ech.ch/xmlns/eCH-0194/1", "handleDelivery");
    public final static String MESSAGE_TYPE = "sedex://personMoveRequest";
    public final static String EXPECTED_MESSAGE_ACTION = WebServiceHeaderActionCodeEnumeration.POSITIVE_REPORT.code();

    // Wert für die Adresse des Web Services aus application.properties
    @Value("${pr.endpoint}")
    private String webserviceUrl;

    public Object identifyPerson(PersonMoveRequest personMoveRequest, String businessCaseId) {
        WebServiceDefaultHeaderHelper defaultHeaderHelper = new WebServiceDefaultHeaderHelper();
        HeaderType headerRequest = defaultHeaderHelper.getHeaderRequest();
        String messageType = MESSAGE_TYPE;
        headerRequest.setMessageType(messageType);
        headerRequest.setMessageId(businessCaseId + "." + messageType);
        headerRequest.setBusinessProcessId(businessCaseId);
        
        DeliveryType deliveryRequest = new DeliveryType();
        deliveryRequest.setDeliveryHeader(headerRequest);
        deliveryRequest.setPersonMoveRequest(personMoveRequest);
        
        HandleDelivery handleDeliveryRequest = new HandleDelivery();
        handleDeliveryRequest.setDelivery(deliveryRequest);

        Object webServiceResponse = webServiceTemplate.marshalSendAndReceive(webserviceUrl,
                new JAXBElement(HANDLE_DELIVERY_QNAME, HandleDelivery.class, handleDeliveryRequest));
 
        Object jAXBElement = ((JAXBElement) webServiceResponse).getValue();
        if (jAXBElement instanceof HandleDeliveryResponse) {
            HandleDeliveryResponse handleDeliveryResponse = (HandleDeliveryResponse) jAXBElement;
            DeliveryType deliveryResponse = handleDeliveryResponse.getDelivery();
            HeaderType headerResponse = deliveryResponse.getDeliveryHeader();
            
            if(headerResponse.getAction().equals(EXPECTED_MESSAGE_ACTION)){
                PersonMoveResponse personMoveResponse = deliveryResponse.getPersonMoveResponse();
                return personMoveResponse;
            } else {
                NegativeReportType negativeReport = deliveryResponse.getNegativeReport();
                InfoType info = negativeReport.getGeneralError().get(0);
                return info;
            }
        } else {
            throw new UnsupportedOperationException("Webservice reutrned unsupported type: " + jAXBElement.getClass().getName());
        }
    }
}
