package ch.zhaw.gpi.eumzugplattform.processdata;


/**
 * Klasse für eine Versicherten-Adresse (verwendet von VeKaClientService)
 * 
 * @author scep
 */
public class VeKaAddress {    
    // Strasse
    private String street;
    
    // Hausnummer
    private String houseNumber;
    
    // PLZ
    private int plz;
    
    // Stadt/Ort
    private String town;
    
    // GETTER und SETTER
    public String getStreet() {
        return street;
    }

    public VeKaAddress setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public VeKaAddress setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public int getPlz() {
        return plz;
    }

    public VeKaAddress setPlz(int plz) {
        this.plz = plz;
        return this;
    }

    public String getTown() {
        return town;
    }

    public VeKaAddress setTown(String town) {
        this.town = town;
        return this;
    }    
}
