package ch.zhaw.gpi.eumzugplattform.processdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Hilfsklasse für Deserialisierung
 * 
 * Diese Hilfsklasse ist lediglich erforderlich, weil bei der Deserialisierung mit 
 * Spin (Jackson) von JSON zurück nach Java-Objekt bei einem List<Person>
 * die Deserialisierung nicht klappt. Es wird dann List<java.util.Object> zurück gegeben
 * statt List<Person>. Tipp von hier: https://forum.camunda.org/t/how-to-properly-de-serialize-collections-to-json/3331/2?u=scepbjoern 
 * 
 * Diese Hilfsklasse enthält daher nichts weiter als eine Kapselung von List<Person>,
 * was als Prozessvariable gespeichert und auch persistiert wird.
 * 
 * @author scep
 */
public final class PersonList implements Serializable {
    private List<Person> persons;
    
    public PersonList(){
        this.persons = new ArrayList<>();
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
    
    public void addPerson(Person person){
        this.persons.add(person);
    }
    
}
