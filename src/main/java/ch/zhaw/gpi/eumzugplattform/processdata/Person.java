package ch.zhaw.gpi.eumzugplattform.processdata;

import java.io.Serializable;
import java.util.Date;

/**
 * Klasse, um die Angaben zu einer (mit)umziehenden Person zu verwalten
 * 
 * Diese Klasse enthält alle Daten im Zusammenhang mit dem eUmzug für eine Person
 * 
 * @author scep
 */
public class Person implements Serializable {
    // Variablen passend zu eCH-044:PersonIdentificationType. Es könnte theoretisch
    // daher diese Klasse einbezogen werden statt all die einzelnen Attribute
    // aber diese generierte Klasse implementiert nicht Serializable, was für
    // Camunda Spin erforderlich ist
    private String firstName;
    private String officialName;
    private String sex;
    private String localPersonId;
    private Date dateOfBirth;
    
    // true, falls es sich um den Meldepflichtigen handelt
    private Boolean isMainPerson;
    
    // Variablen für die Grundversicherungsprüfung
    private Long baseInsuranceNumber;
    private String checkBaseInsuranceResult;
    private String checkBaseInsuranceResultDetails;

    public String getFirstName() {
        return firstName;
    }

    public Person setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getOfficialName() {
        return officialName;
    }

    public Person setOfficialName(String officialName) {
        this.officialName = officialName;
        return this;
    }

    public String getSex() {
        return sex;
    }

    public Person setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public String getLocalPersonId() {
        return localPersonId;
    }

    public Person setLocalPersonId(String localPersonId) {
        this.localPersonId = localPersonId;
        return this;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public Person setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public Boolean getIsMainPerson() {
        return isMainPerson;
    }

    public Person setIsMainPerson(Boolean isMainPerson) {
        this.isMainPerson = isMainPerson;
        return this;
    }
    

    public Long getBaseInsuranceNumber() {
        return baseInsuranceNumber;
    }

    public Person setBaseInsuranceNumber(Long baseInsuranceNumber) {
        this.baseInsuranceNumber = baseInsuranceNumber;
        return this;
    }

    public String getCheckBaseInsuranceResult() {
        return checkBaseInsuranceResult;
    }

    public Person setCheckBaseInsuranceResult(String checkBaseInsuranceResult) {
        this.checkBaseInsuranceResult = checkBaseInsuranceResult;
        return this;
    }

    public String getCheckBaseInsuranceResultDetails() {
        return checkBaseInsuranceResultDetails;
    }

    public Person setCheckBaseInsuranceResultDetails(String checkBaseInsuranceResultDetails) {
        this.checkBaseInsuranceResultDetails = checkBaseInsuranceResultDetails;
        return this;
    }
}
