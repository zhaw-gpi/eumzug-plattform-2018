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
    
    // Versicherte PersonPD (siehe separate Klasse)
    private PersonPD insuredPerson;

    // GETTER und SETTER
    public boolean isBaseInsured() {
        return baseInsured;
    }

    public VeKaCard setBaseInsured(boolean baseInsured) {
        this.baseInsured = baseInsured;
        return this;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public VeKaCard setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public PersonPD getInsuredPerson() {
        return insuredPerson;
    }

    public VeKaCard setInsuredPerson(PersonPD insuredPerson) {
        this.insuredPerson = insuredPerson;
        return this;
    }
}