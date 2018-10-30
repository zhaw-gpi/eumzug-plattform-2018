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
import javax.validation.constraints.Size;

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
    
    // Status-Eintrag
    @NotNull
    @Size(min = 1, max = 100)
    private String status;
    
    // Beziehung zu einer Person
    @ManyToOne
    private PersonEntity person;

    // GETTER und SETTER
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Date getLogTimeStamp() {
        return logTimeStamp;
    }

    public void setLogTimeStamp(Date logTimeStamp) {
        this.logTimeStamp = logTimeStamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PersonEntity getPerson() {
        return person;
    }

    public void setPerson(PersonEntity person) {
        this.person = person;
    }
    
}