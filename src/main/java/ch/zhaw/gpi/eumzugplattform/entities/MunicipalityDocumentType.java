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
 * Enitity-Klasse für Kombination aus Gemeinde und einem Dokumententyp
 * 
 * Da weitere Attribute vorhanden sind, kann nicht @ManyToMany genutzt werden
 *
 * @author scep
 */
@Entity
public class MunicipalityDocumentType implements Serializable {

    // Automatisch gesetzte Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @JoinColumn(name = "DOCUMENT_TYPE_ID")
    private DocumentType documentType;
    
    // Referenz auf eine Gemeinde (wird in Datenbank über Foreign Key implementiert)
    @ManyToOne
    @JoinColumn(name = "MUNICIPALITY_ID")
    private Municipality municipality;
    
    // GETTER UND SETTER
    public DocumentType getDocumentType() {
        return documentType;
    }

    public MunicipalityDocumentType setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
        return this;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public MunicipalityDocumentType setMunicipality(Municipality municipality) {
        this.municipality = municipality;
        return this;
    }
    
    

    public Boolean isStrangerCondition() {
        return strangerCondition;
    }

    public MunicipalityDocumentType setStrangerCondition(Boolean strangerCondition) {
        this.strangerCondition = strangerCondition;
        return this;
    }

    public Boolean isChildrenCondition() {
        return childrenCondition;
    }

    public MunicipalityDocumentType setChildrenCondition(Boolean childrenCondition) {
        this.childrenCondition = childrenCondition;
        return this;
    }

    public Boolean isMarriageCondition() {
        return marriageCondition;
    }

    public MunicipalityDocumentType setMarriageCondition(Boolean marriageCondition) {
        this.marriageCondition = marriageCondition;
        return this;
    }

    public Long getId() {
        return id;
    }

    public MunicipalityDocumentType setId(Long id) {
        this.id = id;
        return this;
    }

}