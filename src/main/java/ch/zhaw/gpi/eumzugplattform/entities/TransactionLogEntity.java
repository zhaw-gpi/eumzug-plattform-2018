package ch.zhaw.gpi.eumzugplattform.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * Enitity-Klasse für Bewegungsdaten zu einem Transaktions-Log-Eintrag der Umzugsplattform
 * 
 * @author scep
 */
@Entity(name="TransactionLog")
public class TransactionLogEntity implements Serializable {

    // Automatisch generierte Id
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long logId;
    
    // Zeitstempel
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date logTimeStamp;
    
    // Referenz auf Status
    @ManyToOne
    private StateEntity state;
    
    // Beziehung zu einer Person
    @ManyToOne
    private PersonEntity person;    
    
    // GETTER und SETTER
    public Long getLogId() {
        return logId;
    }

    public TransactionLogEntity setLogId(Long logId) {
        this.logId = logId;
        return this;
    }

    public Date getLogTimeStamp() {
        return logTimeStamp;
    }

    public TransactionLogEntity setLogTimeStamp(Date logTimeStamp) {
        this.logTimeStamp = logTimeStamp;
        return this;
    }

    public StateEntity getState() {
        return state;
    }

    public TransactionLogEntity setState(StateEntity state) {
        this.state = state;
        return this;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public TransactionLogEntity setPerson(PersonEntity person) {
        this.person = person;
        return this;
    }
    
}