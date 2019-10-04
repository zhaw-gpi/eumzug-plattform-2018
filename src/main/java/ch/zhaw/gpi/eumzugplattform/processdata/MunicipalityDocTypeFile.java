package ch.zhaw.gpi.eumzugplattform.processdata;

import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentType;

/**
 * Klasse, um hochzuladende Dokumenttypen und die zugehörigen Dateien abzubilden
 
 Jedes Objekt dieser Klasse enthält einerseits ein MunicipalityDocumentType-Objekt
 (wird aus der Datenbank gelesen) und anderseits ein String, in welchem 
 der Inhalt der hochgeladenen Datei als DataUrl (fileDataUrl), Base64-encodiert String gespeichert ist
 (wird in Dokumente hochladen-User Task gebildet). -> https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs
 * sowie der Name der Datei (fileName)
 * 
 * @author scep
 */
public class MunicipalityDocTypeFile {
    private MunicipalityDocumentType municipalityDocumentType;
    private String fileDataUrl;
    private String fileName;

    public MunicipalityDocumentType getMunicipalityDocumentType() {
        return municipalityDocumentType;
    }

    public MunicipalityDocTypeFile setMunicipalityDocumentType(MunicipalityDocumentType municipalityDocumentType) {
        this.municipalityDocumentType = municipalityDocumentType;
        return this;
    }

    public String getFileDataUrl() {
        return fileDataUrl;
    }

    public MunicipalityDocTypeFile setFileDataUrl(String fileDataUrl) {
        this.fileDataUrl = fileDataUrl;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public MunicipalityDocTypeFile setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
}
