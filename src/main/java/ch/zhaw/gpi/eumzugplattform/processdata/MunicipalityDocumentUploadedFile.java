package ch.zhaw.gpi.eumzugplattform.processdata;

import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentRelationEntity;
import java.io.Serializable;

/**
 * Klasse, um hochzuladende Dokumente und die zugehörigen Dateien abzubilden
 * 
 * Jedes Objekt dieser Klasse enthält einerseits ein MunicipalityDocumentRelationEntity-Objekt
 * (wird aus der Datenbank gelesen) und anderseits ein String, in welchem 
 * der Inhalt der hochgeladenen Datei als DataUrl (fileDataUrl), Base64-encodiert String gespeichert ist
 * (wird in Dokumente hochladen-User Task gebildet). -> https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs
 * sowie der Name der Datei (fileName)
 * 
 * @author scep
 */
public class MunicipalityDocumentUploadedFile implements Serializable {
    private MunicipalityDocumentRelationEntity municipalityDocumentRelationEntity;
    private String fileDataUrl;
    private String fileName;

    public MunicipalityDocumentRelationEntity getMunicipalityDocumentRelationEntity() {
        return municipalityDocumentRelationEntity;
    }

    public MunicipalityDocumentUploadedFile setMunicipalityDocumentRelationEntity(MunicipalityDocumentRelationEntity municipalityDocumentRelationEntity) {
        this.municipalityDocumentRelationEntity = municipalityDocumentRelationEntity;
        return this;
    }

    public String getFileDataUrl() {
        return fileDataUrl;
    }

    public MunicipalityDocumentUploadedFile setFileDataUrl(String fileDataUrl) {
        this.fileDataUrl = fileDataUrl;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public MunicipalityDocumentUploadedFile setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
}
