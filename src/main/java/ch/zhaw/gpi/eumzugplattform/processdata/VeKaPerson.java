package ch.zhaw.gpi.eumzugplattform.processdata;

import java.util.Date;

/**
 * Klasse für eine versicherte Person (verwendet von VeKaClientService)
 * 
 * @author scep
 */
public class VeKaPerson {
    // Vorname
    private String firstName;
    
    // Nachname
    private String officialName;
    
    // Geburtsdatum
    private Date dateOfBirth;

    // GETTER und SETTER
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
