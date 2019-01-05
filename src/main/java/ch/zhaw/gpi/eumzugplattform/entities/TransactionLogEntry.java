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
@Entity
public class TransactionLogEntry implements Serializable {

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
    private Status status;
    
    // Beziehung zu einer Person
    @ManyToOne
    private Person person;    
    
    // GETTER und SETTER
    public Long getLogId() {
        return logId;
    }

    public TransactionLogEntry setLogId(Long logId) {
        this.logId = logId;
        return this;
    }

    public Date getLogTimeStamp() {
        return logTimeStamp;
    }

    public TransactionLogEntry setLogTimeStamp(Date logTimeStamp) {
        this.logTimeStamp = logTimeStamp;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public TransactionLogEntry setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Person getPerson() {
        return person;
    }

    public TransactionLogEntry setPerson(Person person) {
        this.person = person;
        return this;
    }
    
}