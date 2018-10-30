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
    
    // GETTER UND SETTER
    public DocumentEntity getDocumentEntity() {
        return documentEntity;
    }

    public void setDocumentEntity(DocumentEntity documentEntity) {
        this.documentEntity = documentEntity;
    }

    public Boolean isStrangerCondition() {
        return strangerCondition;
    }

    public void setStrangerCondition(Boolean strangerCondition) {
        this.strangerCondition = strangerCondition;
    }

    public Boolean isChildrenCondition() {
        return childrenCondition;
    }

    public void setChildrenCondition(Boolean childrenCondition) {
        this.childrenCondition = childrenCondition;
    }

    public Boolean isMarriageCondition() {
        return marriageCondition;
    }

    public void setMarriageCondition(Boolean marriageCondition) {
        this.marriageCondition = marriageCondition;
    }

    public Long getMunicipalityDocumentId() {
        return municipalityDocumentId;
    }

    public void setMunicipalityDocumentId(Long municipalityDocumentId) {
        this.municipalityDocumentId = municipalityDocumentId;
    }

}