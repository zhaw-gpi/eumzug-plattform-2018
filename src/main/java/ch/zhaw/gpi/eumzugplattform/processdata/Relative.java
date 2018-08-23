package ch.zhaw.gpi.eumzugplattform.processdata;

import java.io.Serializable;
import java.util.Date;

/**
 * Klasse, um die Angaben zu einer mitumziehenden Person zu verwalten
 * 
 * Eigentlich wäre diese Klasse überflüssig, da sie lediglich Informationen aus der
 * generierten eCH-Klasse PersonMoveResponse.RelatedPerson repliziert. Sie ist allerdings
 * erforderlich, weil die genannte Klasse nicht java.io.Serializable implementiert, was
 * aber für die (De-)Serialisierung über Camunda Spin erforderlich ist. Um die generierten
 * Klassen nicht anzutasten, halt dieses Hilfskonstrukt angelegt.
 * 
 * @author scep
 */
public class Relative implements Serializable {
    // Variablen passend zu eCH-044:PersonIdentificationType
    private String firstName;
    private String officialName;
    private String sex;
    private String localPersonId;
    private Date dateOfBirth;
    
    // Zusätzliche Variable, um zu speichern, ob diese Person mit dem Meldepflichtigen mitumzieht
    private Boolean moveAlong;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLocalPersonId() {
        return localPersonId;
    }

    public void setLocalPersonId(String localPersonId) {
        this.localPersonId = localPersonId;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Boolean getMoveAlong() {
        return moveAlong;
    }

    public void setMoveAlong(Boolean moveAlong) {
        this.moveAlong = moveAlong;
    }
    
    
}
