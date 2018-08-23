package ch.zhaw.gpi.eumzugplattform.processdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse für Deserialisierung
 * 
 * Diese Hilfsklasse ist lediglich erforderlich, weil bei der Deserialisierung mit 
 * Spin (Jackson) von JSON zurück nach Java-Objekt bei einem List<Relative>
 * die Deserialisierung nicht klappt. Es wird dann List<java.util.Object> zurück gegeben
 * statt List<Relative>. Tipp von hier: https://forum.camunda.org/t/how-to-properly-de-serialize-collections-to-json/3331/2?u=scepbjoern 
 * 
 * Diese Hilfsklasse enthält daher nichts weiter als eine Kapselung von List<Relative>,
 * was als Prozessvariable gespeichert und auch persistiert wird.
 * 
 * @author scep
 */
public final class RelativesList implements Serializable {
    private List<Relative> relatives;
    
    public RelativesList(){
        this.relatives = new ArrayList<>();
    }

    public List<Relative> getRelatives() {
        return relatives;
    }

    public void setRelatives(List<Relative> relatives) {
        this.relatives = relatives;
    }
    
    public void addRelative(Relative relative){
        this.relatives.add(relative);
    }
    
}
