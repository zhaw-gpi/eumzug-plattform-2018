package ch.zhaw.gpi.eumzugplattform.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Enitity-Klasse für Stammdaten zu einer politischen Gemeinde
 * 
 * @author scep
 */
@Entity
public class Municipality implements Serializable {

    // Manuell gesetzte Id (BFS-Nummer)
    @Id
    @Min(value = 1)
    @Max(value = 9999)
    private int municipalityId;
    
    // Offizieller Name
    @NotNull
    @Size(min = 1, max = 40)
    private String municipalityName;
    
    // Umzugsgebühr
    @Min(value=0)
    @Max(value=50)
    private int feeMove;
    
    // Wegzugsgebühr
    @Min(value = 0)
    @Max(value = 50)
    private int feeMoveOut;

    // Zuzugsgebühr
    @Min(value = 0)
    @Max(value = 50)
    private int feeMoveIn;
    
    // GETTER UND SETTER
    public int getMunicipalityId() {
        return municipalityId;
    }

    public Municipality setMunicipalityId(int municipalityId) {
        this.municipalityId = municipalityId;
        return this;
    }
    
    public String getMunicipalityName() {
        return municipalityName;
    }

    public Municipality setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
        return this;
    }    

    public int getFeeMove() {
        return feeMove;
    }

    public Municipality setFeeMove(int feeMove) {
        this.feeMove = feeMove;
        return this;
    }    

    public int getFeeMoveOut() {
        return this.feeMoveOut;
    }

    public Municipality setFeeMoveOut(int feeMoveOut) {
        this.feeMoveOut = feeMoveOut;
        return this;
    }

    public int getFeeMoveIn() {
        return this.feeMoveIn;
    }

    public Municipality setFeeMoveIn(int feeMoveIn) {
        this.feeMoveIn = feeMoveIn;
        return this;
    }
}