package ch.zhaw.gpi.eumzugplattform.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Enitity-Klasse für Stammdaten zu einem Dokumententyp (z.B. Heiratsurkunde)
 *
 * @author scep
 */
@Entity
public class DocumentType {

    // Manuell gesetzte Id
    @Id
    private int id;

    // Bezeichnung des Dokuments
    @NotNull
    @Column(unique = true)
    private String name;
    
    // GETTER UND SETTER
    public int getId() {
        return id;
    }

    public DocumentType setId(int id) {
        this.id = id;
        return this;
    }
    
    public String getName() {
        return name;
    }

    public DocumentType setName(String name) {
        this.name = name;
        return this;
    }

}
