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

    public MunicipalityEntity setMunicipalityId(int municipalityId) {
        this.municipalityId = municipalityId;
        return this;
    }
    
    public String getMunicipalityName() {
        return municipalityName;
    }

    public MunicipalityEntity setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
        return this;
    }    

    public int getFeeMove() {
        return feeMove;
    }

    public MunicipalityEntity setFeeMove(int feeMove) {
        this.feeMove = feeMove;
        return this;
    }    

    public int getFeeMoveOut() {
        return this.feeMoveOut;
    }

    public MunicipalityEntity setFeeMoveOut(int feeMoveOut) {
        this.feeMoveOut = feeMoveOut;
        return this;
    }

    public int getFeeMoveIn() {
        return this.feeMoveIn;
    }

    public MunicipalityEntity setFeeMoveIn(int feeMoveIn) {
        this.feeMoveIn = feeMoveIn;
        return this;
    }
    
    public List<MunicipalityDocumentRelationEntity> getMunicipalityDocumentRelationEntities() {
        return municipalityDocumentRelationEntities;
    }

    public MunicipalityEntity setMunicipalityDocumentRelationEntities(List<MunicipalityDocumentRelationEntity> municipalityDocumentRelationEntities) {
        this.municipalityDocumentRelationEntities = municipalityDocumentRelationEntities;
        return this;
    }
    
    // Hinzufügen von einer neuen MunicipalityDocumentRelationEntity
    public MunicipalityEntity addMunicipalityDocumentRelationEntity(MunicipalityDocumentRelationEntity municipalityDocumentRelationEntity) {
        this.municipalityDocumentRelationEntities.add(municipalityDocumentRelationEntity);
        return this;
    }
    
    // Entfernen einer MunicipalityDocumentRelationEntity
    public MunicipalityEntity removeMunicipalityDocumentRelationEntity(MunicipalityDocumentRelationEntity municipalityDocumentRelationEntity) {
        this.municipalityDocumentRelationEntities.remove(municipalityDocumentRelationEntity);
        return this;
    }
    
}