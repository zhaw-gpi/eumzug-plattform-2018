package ch.zhaw.gpi.eumzugplattform.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Enitity-Klasse für Stammdaten zu einem Dokument
 *
 * @author scep
 */
@Entity(name = "Document")
public class DocumentEntity implements Serializable {

    // Manuell gesetzte Id
    @Id
    private int documentId;

    // Bezeichnung des Dokuments
    @NotNull
    @Column(unique = true)
    private String name;
    
    // GETTER UND SETTER
    public int getDocumentId() {
        return documentId;
    }

    public DocumentEntity setDocumentId(int documentId) {
        this.documentId = documentId;
        return this;
    }
    
    public String getName() {
        return name;
    }

    public DocumentEntity setName(String name) {
        this.name = name;
        return this;
    }

}
