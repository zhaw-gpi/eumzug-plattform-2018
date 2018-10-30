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

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
