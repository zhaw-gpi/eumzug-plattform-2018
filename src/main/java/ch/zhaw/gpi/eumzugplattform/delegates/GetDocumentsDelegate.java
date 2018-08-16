package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.entities.DocumentList;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentRelationEntity;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityDocumentUploadedFile;
import ch.zhaw.gpi.eumzugplattform.entities.MunicipalityEntity;
import ch.zhaw.gpi.eumzugplattform.repositories.MunicipalityRepository;
import java.util.List;
import java.util.Optional;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Serialisierte Prozessvariable mit relevanten Dokumenten basierend auf Angaben
 * in der Umzugsplattform-Datenbank erstellen
 *
 * Über ein Data Access Object sollen die Dokumente der relevanten
 * MunicipalityEntity-Objekte aus der Datenbank in ein List Objekt eingelesen
 * werden.
 * 
 * Von dieser Liste wird für jeden Eintrag ein Objekt der Klasse MunicipalityDocumentUploadedFile
 * erzeugt, welches einerseits den Eintrag erhält, aber auch eine Eigenschaft file,
 * welche dann im "Dokumente hochladen"-User Task mit Inhalt gefüllt wird.
 * 
 * Damit keine Deserialisierungs-Probleme auftreten (Details siehe in JavaDoc der Klasse
 * DocumentList), benötigt es ein Hilfsobjekt der Klasse DocumentList.
 * 
 * Dieses Hilfsobjekt wird mittels Camunda Spin in ein JSON-Objekt
 * serialisiert und der Prozessvariable documentListSerialized zugewiesen. Der
 * Prozessvariable documentsExist wird mit true/fals mitgegeben, ob Dokumente
 * existieren.
 */
@Named("getDocumentsAdapter")
public class GetDocumentsDelegate implements JavaDelegate {

    // Das für die Datenbankabfragen zuständige Data Access Object wird als Dependency injiziert
    @Autowired
    private MunicipalityRepository municipalityRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {

        // Prozess Variable municipalityIdMoveIn wird der Variable municipalityIdMoveIn zugewiesen.
        int municipalityIdMoveIn = (int) execution.getVariable("municipalityIdMoveIn");

        //MunicipalityResult wird anhand der Id ausgelesen
        Optional<MunicipalityEntity> moveInMunicipalityResult = municipalityRepository.findById(municipalityIdMoveIn);

        // Variable für MunicipalityDocumentRelationEntity-Objekte initialisieren
        List<MunicipalityDocumentRelationEntity> municipalityDocuments;

        // Die Variable ObjectValue wird mit null initialisiert
        ObjectValue documentListpSerialized = null;

        // Die Variable documentsExist wird mit false initialisiert
        Boolean documentsExist = false;

        // Prüfen, ob auch wirklich ein Result zurückgegeben wurde
        if (moveInMunicipalityResult.isPresent()) {
            // Falls ja, alle MunicipalityDocumentRelationEntity-Objekte aus dem MunicipalityEntity Objekt als Liste erhalten
            municipalityDocuments = moveInMunicipalityResult.get().getMunicipalityDocumentRelationEntities();

            // Prüfen ob die municipalityDocuments Liste nicht null und nicht leer ist. 
            // Nur falls mind. 1 Dokument in der Liste vorhanden ist, wird die Variable documentsExist 
            // auf true gesetzt und die Liste mittels Camunda Spin ins JSON-Format serialisiert
            if (municipalityDocuments != null && municipalityDocuments.size() > 0) {
                DocumentList documentList = new DocumentList();
                
                for (MunicipalityDocumentRelationEntity municipalityDocument : municipalityDocuments) {
                    MunicipalityDocumentUploadedFile municipalityDocumentUploadedFile = new MunicipalityDocumentUploadedFile();
                    municipalityDocumentUploadedFile.setMunicipalityDocumentRelationEntity(municipalityDocument);
                    documentList.addMunicipalityDocumentUploadedFile(municipalityDocumentUploadedFile);
                }
                // Diese Liste mittels Camunda Spin ins JSON-Format serialisieren
                documentListpSerialized = Variables.objectValue(documentList)
                        .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                        .create();
                documentsExist = true;
            }
        } // Falls nicht, dann muss nichts gemacht werden, weil die entsprechenden Variablen schon gesetzt sind

        // Die serialisierte Dokumentenliste einer Prozessvariable zuweisen
        execution.setVariable("documentListSerialized", documentListpSerialized);
        // Die Variable documentsExists einer Prozessvariable zuweisen
        execution.setVariable("documentsExist", documentsExist);
    }
}
