package ch.zhaw.gpi.eumzugplattform.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

/**
 * Konfiguriert für alle Webservice Clients gültige Eigenschaften (Web Service Template)
 * 
 * Den meisten Code habe ich von https://www.codenotfound.com/2016/10/spring-ws-soap-web-service-consumer-provider-wsdl-example.html
 * @author scep
 */
@Configuration
public class GenericWebServiceClientConfiguration {
    
    public static final String CONTEXT_PATH = "ch.zhaw.iwi.gpi.gwr:ch.ech.xmlns.ech_0194._1";
    
    /**
     * Bean für das Marshaling initialisieren (Serializierung) damit es während der Laufzeit verfügbar ist
     * @return 
     */
    @Bean
    public Jaxb2Marshaller jaxb2Marshaller(){
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setContextPath(CONTEXT_PATH);
        return jaxb2Marshaller;
    }
    
    /** 
     * Webservice Template Bean damit es während der Laufzeit verfügbar ist
     * @return 
     */
    @Bean
    public WebServiceTemplate webServiceTemplate(){
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(jaxb2Marshaller());
        webServiceTemplate.setUnmarshaller(jaxb2Marshaller());
        return webServiceTemplate;
    }
}
