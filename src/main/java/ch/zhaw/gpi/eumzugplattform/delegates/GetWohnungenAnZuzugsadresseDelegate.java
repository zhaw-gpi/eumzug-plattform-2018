package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.webserviceclients.GwrWebServiceClient;
import ch.zhaw.iwi.gpi.gwr.AdresseType;
import ch.zhaw.iwi.gpi.gwr.WohnungType;
import ch.zhaw.iwi.gpi.gwr.WohnungenAntwortType;
import java.util.List;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Überprüfen ob für die angegebene Zuzugsadresse Wohnungen im GWR vorhanden
 * sind
 *
 * Die Operation wohnungenInGebaeudeOperation des Gebäude- und Wohnungsregister-Web Service (http://localhost:8090/soap/GebaeudeUndWohnungsRegisterService) wird synchron aufgerufen. 
 * 
 * Der eigentliche Web Service-Client (GwrWebServiceClient) für die Kommunikation mit diesem Service ist in einer eigenen Klasse enthalten.
 *
 * Die Aufgabe dieser Klasse ist zunächst, die folgenden Prozessvariablen den passenden Eigenschaften der AdresseType-Klasse zuzuweisen: houseNumberMoveIn, swissZipCodeMoveIn, streetMoveIn.
 *
 * Hinweis: Die AdresseType-Klasse wird gebildet über ein Build-Plugin, das im pom.xml konfiguriert ist.
 * 
 * Mit der AdresseType-Klasse kann nun der GwrWebServiceClient die Methode wohnungenInGebaeudeOperation ausführen, welche als Antwort ein Objekt der Klasse WohnungenAntwortType zurückgibt.
 * 
 * Womit wir bei der zweiten Aufgabe dieser Klasse sind, nämlich dem Interpretieren der WohnungenAntwortType: Die darin enthaltene Methode getWohnungenAntwort() liefert eine Liste von verfügbaren Wohnungen zurück. Diese Liste wird mit Camunda Spin serialisiert und der Prozessvariable wohnungenList übergeben.
 * 
 */
@Named("getWohnungenAnZuzugsadresseAdapter")
public class GetWohnungenAnZuzugsadresseDelegate implements JavaDelegate {

    // Entsprechende Bean wird instanziert, Referenz ist vorhanden
    @Autowired
    private GwrWebServiceClient gebaeudeUndWohnungsRegisterClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Variablen Deinnr, dplz4 und strname setzen mit den jewiligen Werten
        // aus der processVariables-Map
        String deinr = (String) execution.getVariable("houseNumberMoveIn");
        Integer dplz4 = (Integer) execution.getVariable("swissZipCodeMoveIn");
        String strname = (String) execution.getVariable("streetMoveIn");

        // Instanzierung eines Objektes vom Typ AdresseType
        AdresseType adresse = new AdresseType();

        // Werte des AdresseType abfüllen
        adresse.setDEINR(deinr);
        adresse.setDPLZ4(dplz4);
        adresse.setSTRNAME(strname);

        // Client des GWR aufrufen und die Methode wohnungenAnfrage() ausführen
        WohnungenAntwortType wohnungenAntwortType = gebaeudeUndWohnungsRegisterClient.wohnungenAnfrage(adresse);
        
        // Leere Liste vom Typ WohnungType instanzieren
        List<WohnungType> wohnungenList = null;

        Boolean wohnungenGefunden = false;
        
        // Überprüfen ob die Response Wohnungen enthält...
        if (wohnungenAntwortType.getWohnung() != null && wohnungenAntwortType.getWohnung().size() > 0) {
            // ... Es wurden Wohnungen gefunden.
            
            // Wohnungen der instanzierten Liste wohnungenListe übergeben
            wohnungenList = wohnungenAntwortType.getWohnung();
            
            wohnungenGefunden = true;
        }

        // Die  Wohnungsliste einer Prozessvariable zuweisen
        execution.setVariable("wohnungenList", wohnungenList);
        
        execution.setVariable("wohnungenGefunden", wohnungenGefunden);
    }
}
