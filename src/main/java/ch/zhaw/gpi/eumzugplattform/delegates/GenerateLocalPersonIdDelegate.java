package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.services.LocalPersonIdGeneratorService;
import java.util.Date;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Generiert eine neue lokale PersonenId Als Id wird die AHV-Nummer gewählt,
 * sofern vorhanden, ansonsten ein Zusammenzug aus je 3 Buchstaben Nachnamen und
 * Vornamen sowie Geburtsdatum und Geschlecht, also z.B. aus Ruth Müller,
 * Weiblich (2), 1.1.1980 wird "RutMül_1980-01-01_2".
 */
@Named("generateLocalPersonIdAdapter")
public class GenerateLocalPersonIdDelegate implements JavaDelegate {
    
    @Autowired
    private LocalPersonIdGeneratorService localPersonIdGenerator;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Prozessvariablen auslesen
        Long vn = (Long) execution.getVariable("vn");
        String firstName = (String) execution.getVariable("firstName");
        String officialName = (String) execution.getVariable("officialName");
        Date dateOfBirth = (Date) execution.getVariable("dateOfBirth");
        Integer sex = (Integer) execution.getVariable("sex");
        
        // Lokale PersonenId generieren
        String localPersonId = localPersonIdGenerator.generateId(vn, firstName, officialName, dateOfBirth, sex.toString());

        // Die generierte localPersonId soll der entsprechenden Prozessvariable
        // zugewiesen werden
        execution.setVariable("localPersonId", localPersonId);
    }
}
