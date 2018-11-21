package ch.zhaw.gpi.eumzugplattform.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Enitity-Klasse für Stammdaten zu Status
 * 
 * @author Stefan Fischer, adaptiert von scep
 */
@Entity(name = "State")
public class StateEntity implements Serializable{
    // Automatisch gesetzte Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stateId;
    
    // Name von Status z.B. FINISHED_SUCCESSFUL
    @NotNull
    private String name;
    
    // Deutsche Beschreibung von Status z.B. Erfolgreicher Abschluss des eUmzugsmeldung
    @NotNull
    private String germanText;
    
    // Englische Beschreibung von Status z.B. Successful completion of the eRelocation report
    @NotNull
    private String englishText;
    
    // GETTER und SETTER

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGermanText() {
        return germanText;
    }

    public void setGermanText(String germanText) {
        this.germanText = germanText;
    }

    public String getEnglishText() {
        return englishText;
    }

    public void setEnglishText(String englishText) {
        this.englishText = englishText;
    }
    
}
