package ch.zhaw.gpi.eumzugplattform.processdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse für Deserialisierung von List<FilesOfPerson>
 * 
 * Diese Hilfsklasse ist lediglich erforderlich, weil bei der Deserialisierung mit 
 * Spin (Jackson) von JSON zurück nach Java-Objekt die Deserialisierung nicht klappt. 
 * Es wird dann List<java.util.Object> zurück gegeben. 
 * Tipp von hier: https://forum.camunda.org/t/how-to-properly-de-serialize-collections-to-json/3331/2?u=scepbjoern
 * 
 * @author tcz05 und scep
 */
public final class FilesOfPersonList implements Serializable {
    private List<FilesOfPerson> filesOfPersonList;
    
    public FilesOfPersonList(){
        this.filesOfPersonList = new ArrayList<>();
    }
    
    public List<FilesOfPerson> getFilesOfPersonList() {
        return filesOfPersonList;
    }

    public void setFilesOfPersonList(List<FilesOfPerson> filesOfPersonList) {
        this.filesOfPersonList = filesOfPersonList;
    }
    
    public void addFilesOfPerson(FilesOfPerson filesOfPerson){
        this.filesOfPersonList.add(filesOfPerson);
    }
    
}
