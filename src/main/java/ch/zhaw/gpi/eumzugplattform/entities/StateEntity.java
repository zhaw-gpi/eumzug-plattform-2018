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

    public StateEntity setStateId(Long stateId) {
        this.stateId = stateId;
        return this;
    }

    public String getName() {
        return name;
    }

    public StateEntity setName(String name) {
        this.name = name;
        return this;
    }

    public String getGermanText() {
        return germanText;
    }

    public StateEntity setGermanText(String germanText) {
        this.germanText = germanText;
        return this;
    }

    public String getEnglishText() {
        return englishText;
    }

    public StateEntity setEnglishText(String englishText) {
        this.englishText = englishText;
        return this;
    }
    
}
