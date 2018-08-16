package ch.zhaw.gpi.eumzugplattform.entities;

import java.io.File;
import java.io.Serializable;
import java.util.Optional;

/**
 * Klasse, hochzuladende Dokumente und die zugehörigen Dateien abzubilden
 * 
 * Jedes Objekt dieser Klasse enthält einerseits ein MunicipalityDocumentRelationEntity-Objekt
 * (wird aus der Datenbank gelesen) und anderseits ein File-Objekt (wird in Dokumente hochladen-
 * User Task gebildet.
 * 
 * @author scep
 */
public class MunicipalityDocumentUploadedFile implements Serializable {
    private MunicipalityDocumentRelationEntity municipalityDocumentRelationEntity;
    private Optional<File> file;

    public MunicipalityDocumentRelationEntity getMunicipalityDocumentRelationEntity() {
        return municipalityDocumentRelationEntity;
    }

    public void setMunicipalityDocumentRelationEntity(MunicipalityDocumentRelationEntity municipalityDocumentRelationEntity) {
        this.municipalityDocumentRelationEntity = municipalityDocumentRelationEntity;
    }

    public Optional<File> getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = Optional.ofNullable(file);
    }
}
