package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.webserviceclients.GwrWebServiceClient;
import ch.zhaw.iwi.gpi.gwr.AddresseExistenzType;
import ch.zhaw.iwi.gpi.gwr.AdresseType;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Wegzugsadress-Eingaben auf Übereinstimmung in Gebäude- und Wohnungsregister prüfen
 * 
 * Die Operation adressPruefungOperation des Gebäude- und Wohnungsregister-Web Service (http://localhost:8090/soap/GebaeudeUndWohnungsRegisterService) wird synchron aufgerufen. 
 * 
 * Der eigentliche Web Service-Client (GwrWebServiceClient) für die Kommunikation mit diesem Service ist in einer eigenen Klasse enthalten.
 * 
 * Die Aufgabe dieser Klasse ist zunächst, die folgenden Prozessvariablen den passenden Eigenschaften der AdresseType-Klasse zuzuweisen: houseNumberMoveOut, swissZipCodeMoveOut, streetMoveOut.
 * 
 * Hinweis: Die AdresseType-Klasse wird gebildet über ein Build-Plugin, das im pom.xml konfiguriert ist.
 * 
 * Mit der AdresseType-Klasse kann nun der GwrWebServiceClient die Methode adressPruefungOperation ausführen, welche als Antwort ein Objekt der Klasse AddresseExistenzType zurückgibt.
 * 
 * Womit wir bei der zweiten Aufgabe dieser Klasse sind, nämlich dem Interpretieren der AddresseExistenzType: Es wird die in AddresseExistenzType enthaltene Eigenschaft Exists der Prozessvariable adressMoveOutExists übergeben.
 */
@Named("checkAdressMoveOutAdapter")
public class CheckAdressMoveOutDelegate implements JavaDelegate {

    @Autowired
    private GwrWebServiceClient gebaeudeUndWohnungsRegisterClient;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Für den Request an den Web Service müssen die Prozessvariablen den 
        // passenden Eigenschaften der AddresseType-Klasse zugewiesen werden.
        String deinr = (String) execution.getVariable("houseNumberMoveOut");
        Integer dplz4 = (Integer) execution.getVariable("swissZipCodeMoveOut");
        String strname = (String) execution.getVariable("streetMoveOut");

        AdresseType adresse = new AdresseType();
        adresse.setDEINR(deinr);
        adresse.setDPLZ4(dplz4);
        adresse.setSTRNAME(strname);

        // Die Response ist vom Typ AddresseExistenzType.
        AddresseExistenzType addresseExistenz = gebaeudeUndWohnungsRegisterClient.adressPruefungOperation(adresse);

        // Die darin enthaltene Eigenschaft EXISTS wird der Prozessvariable adressMoveOutExists übergeben.
        Boolean adressMoveOutExists = addresseExistenz.isEXISTS();
        execution.setVariable("adressMoveOutExists", adressMoveOutExists);
    }

}
