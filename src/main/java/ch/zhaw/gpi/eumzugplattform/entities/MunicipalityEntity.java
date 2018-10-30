package ch.zhaw.gpi.eumzugplattform.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Enitity-Klasse für Stammdaten zu einer politischen Gemeinde
 * 
 * @author scep
 */
@Entity(name = "Municipality")
public class MunicipalityEntity implements Serializable {

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
    
    // Liste benötigter Dokumente inkl. Hochlad-Bedingungen
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "MUNICIPALITY_ID")
    private List<MunicipalityDocumentRelationEntity> municipalityDocumentRelationEntities;
    
    // GETTER UND SETTER
    public int getMunicipalityId() {
        return municipalityId;
    }

    public void setMunicipalityId(int municipalityId) {
        this.municipalityId = municipalityId;
    }
    
    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }    

    public int getFeeMove() {
        return feeMove;
    }

    public void setFeeMove(int feeMove) {
        this.feeMove = feeMove;
    }    

    public int getFeeMoveOut() {
        return this.feeMoveOut;
    }

    public void setFeeMoveOut(int feeMoveOut) {
        this.feeMoveOut = feeMoveOut;
    }

    public int getFeeMoveIn() {
        return this.feeMoveIn;
    }

    public void setFeeMoveIn(int feeMoveIn) {
        this.feeMoveIn = feeMoveIn;
    }
    
    public List<MunicipalityDocumentRelationEntity> getMunicipalityDocumentRelationEntities() {
        return municipalityDocumentRelationEntities;
    }

    public void setMunicipalityDocumentRelationEntities(List<MunicipalityDocumentRelationEntity> municipalityDocumentRelationEntities) {
        this.municipalityDocumentRelationEntities = municipalityDocumentRelationEntities;
    }
    
    // Hinzufügen von einer neuen MunicipalityDocumentRelationEntity
    public void addMunicipalityDocumentRelationEntity(MunicipalityDocumentRelationEntity municipalityDocumentRelationEntity) {
        this.municipalityDocumentRelationEntities.add(municipalityDocumentRelationEntity);
    }
    
    // Entfernen einer MunicipalityDocumentRelationEntity
    public void removeMunicipalityDocumentRelationEntity(MunicipalityDocumentRelationEntity municipalityDocumentRelationEntity) {
        this.municipalityDocumentRelationEntities.remove(municipalityDocumentRelationEntity);
    }
    
}