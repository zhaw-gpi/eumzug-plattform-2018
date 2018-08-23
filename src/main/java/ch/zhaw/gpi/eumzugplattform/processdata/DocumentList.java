package ch.zhaw.gpi.eumzugplattform.processdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse für Deserialisierung
 * 
 * Diese Hilfsklasse ist lediglich erforderlich, weil bei der Deserialisierung mit 
 * Spin (Jackson) von JSON zurück nach Java-Objekt bei einem List<MunicipalityDocumentUploadedFile>
 * die Deserialisierung nicht klappt. Es wird dann List<java.util.Object> zurück gegeben
 * statt List<MunicipalityDocumentUploadedFile>. Tipp von hier: https://forum.camunda.org/t/how-to-properly-de-serialize-collections-to-json/3331/2?u=scepbjoern 
 * 
 * Diese Hilfsklasse enthält daher nichts weiter als eine Kapselung von List<MunicipalityDocumentUploadedFile>,
 * was als Prozessvariable gespeichert und auch persistiert wird.
 * 
 * @author scep
 */
public final class DocumentList implements Serializable {
    private List<MunicipalityDocumentUploadedFile> municipalityDocumentUploadedFiles;
    
    public DocumentList(){
        this.municipalityDocumentUploadedFiles = new ArrayList<>();
    }

    public List<MunicipalityDocumentUploadedFile> getMunicipalityDocumentUploadedFiles() {
        return municipalityDocumentUploadedFiles;
    }

    public void setMunicipalityDocumentUploadedFiles(List<MunicipalityDocumentUploadedFile> municipalityDocumentUploadedFiles) {
        this.municipalityDocumentUploadedFiles = municipalityDocumentUploadedFiles;
    }
    
    public void addMunicipalityDocumentUploadedFile(MunicipalityDocumentUploadedFile municipalityDocumentUploadedFile){
        this.municipalityDocumentUploadedFiles.add(municipalityDocumentUploadedFile);
    }
    
}
