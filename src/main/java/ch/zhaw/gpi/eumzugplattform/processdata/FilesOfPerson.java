package ch.zhaw.gpi.eumzugplattform.processdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Liste von Dateien/Dokumenten zu einer Person
 * 
 * @author scep und tzc05
 */
public final class FilesOfPerson implements Serializable {
    // Liste von Gemeinde-Dokumenttypen-Dateien
    private List<MunicipalityDocTypeFile> municipalityDocTypeFiles;
    
    // Eine (mitumziehende) Person
    private PersonPD person;
    
    public FilesOfPerson(){
        this.municipalityDocTypeFiles = new ArrayList<>();
    }

    public List<MunicipalityDocTypeFile> getMunicipalityDocTypeFiles() {
        return municipalityDocTypeFiles;
    }

    public void setMunicipalityDocTypeFiles(List<MunicipalityDocTypeFile> municipalityDocTypeFiles) {
        this.municipalityDocTypeFiles = municipalityDocTypeFiles;
    }
    
    public void addMunicipalityDocTypeFile(MunicipalityDocTypeFile municipalityDocTypeFile){
        this.municipalityDocTypeFiles.add(municipalityDocTypeFile);
    }

    public PersonPD getPerson() {
        return person;
    }

    public void setPerson(PersonPD person) {
        this.person = person;
    }
}
