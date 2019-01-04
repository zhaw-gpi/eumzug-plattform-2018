package ch.zhaw.gpi.eumzugplattform.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * Enitity-Klasse für Kombination aus Gemeinde und einem Dokument
 * 
 * Da weitere Attribute vorhanden sind, kann nicht @ManyToMany genutzt werden
 *
 * @author scep
 */
@Entity(name = "MunicipalityDocumentRelation")
public class MunicipalityDocumentRelationEntity implements Serializable {

    // Automatisch gesetzte Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long municipalityDocumentId;

    // Hochladen-Bedingung Zivilstand (nur wenn verheiratet)
    @NotNull
    private Boolean marriageCondition;

    // Hochladen-Bedingung Elternschaft (nur mit Kindern)
    @NotNull
    private Boolean childrenCondition;

    // Hochladen-Bedingung Ausländerstatus (nur wenn Ausländer)
    @NotNull
    private Boolean strangerCondition;
    
    // Referenz auf ein Dokument (wird in Datenbank über Foreign Key implementiert)
    @ManyToOne
    @JoinColumn(name = "DOCUMENT_ID")
    private DocumentEntity documentEntity;
    
    // Referenz auf eine Gemeinde (wird in Datenbank über Foreign Key implementiert)
    @ManyToOne
    @JoinColumn(name = "MUNICIPALITY_ID")
    private MunicipalityEntity municipalityEntity;
    
    // GETTER UND SETTER
    public DocumentEntity getDocumentEntity() {
        return documentEntity;
    }

    public MunicipalityDocumentRelationEntity setDocumentEntity(DocumentEntity documentEntity) {
        this.documentEntity = documentEntity;
        return this;
    }

    public MunicipalityEntity getMunicipalityEntity() {
        return municipalityEntity;
    }

    public MunicipalityDocumentRelationEntity setMunicipalityEntity(MunicipalityEntity municipalityEntity) {
        this.municipalityEntity = municipalityEntity;
        return this;
    }
    
    

    public Boolean isStrangerCondition() {
        return strangerCondition;
    }

    public MunicipalityDocumentRelationEntity setStrangerCondition(Boolean strangerCondition) {
        this.strangerCondition = strangerCondition;
        return this;
    }

    public Boolean isChildrenCondition() {
        return childrenCondition;
    }

    public MunicipalityDocumentRelationEntity setChildrenCondition(Boolean childrenCondition) {
        this.childrenCondition = childrenCondition;
        return this;
    }

    public Boolean isMarriageCondition() {
        return marriageCondition;
    }

    public MunicipalityDocumentRelationEntity setMarriageCondition(Boolean marriageCondition) {
        this.marriageCondition = marriageCondition;
        return this;
    }

    public Long getMunicipalityDocumentId() {
        return municipalityDocumentId;
    }

    public MunicipalityDocumentRelationEntity setMunicipalityDocumentId(Long municipalityDocumentId) {
        this.municipalityDocumentId = municipalityDocumentId;
        return this;
    }

}