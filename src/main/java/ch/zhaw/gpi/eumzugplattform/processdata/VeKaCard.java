package ch.zhaw.gpi.eumzugplattform.processdata;

import java.util.Date;

/**
 * Klasse für eine Versicherten-Karte (verwendet von VeKaClientService)
 * 
 * @author scep
 */
public class VeKaCard {
    // Grundversicherung enthalten?
    private boolean baseInsured;
    
    // Ablaufdatum
    private Date expiryDate;
    
    // Versicherte Person (siehe separate Klasse)
    private VeKaPerson insuredPerson;

    // GETTER und SETTER
    public boolean isBaseInsured() {
        return baseInsured;
    }

    public void setBaseInsured(boolean baseInsured) {
        this.baseInsured = baseInsured;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public VeKaPerson getInsuredPerson() {
        return insuredPerson;
    }

    public void setInsuredPerson(VeKaPerson insuredPerson) {
        this.insuredPerson = insuredPerson;
    }
}