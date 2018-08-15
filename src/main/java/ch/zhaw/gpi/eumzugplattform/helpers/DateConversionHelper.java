package ch.zhaw.gpi.eumzugplattform.helpers;

import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Hilfsklasse für die Konversion von XML- und Java-Datumstypen
 */
public class DateConversionHelper {
    
    /**
     * Wandelt ein java.util.Date in ein XMLGregorianCalendar-Datum um
     * @param date
     * @return 
     */
    public static XMLGregorianCalendar DateToXMLGregorianCalendar(Date date){
        // Initalisieren einer neuen GregorianCalendar-Variable als Zwischenschritt
        GregorianCalendar gregorianCalendar = new GregorianCalendar();

        // Dieser GregorianCalendar das java.util-Datum zuweisen
        gregorianCalendar.setTime(date);

        // Die sogenannte DataTypeFactory nutzen, um das GregorianCalendar-Objekt
        // in ein XMLGregorianCalendar-Objekt zu übergeben. Sollte dies wider
        // Erwarten fehlschlagen, wird eine Pseudo-Fehlerbehandlung vorgenommen
        try{
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        }
        catch(DatatypeConfigurationException e){
            throw new RuntimeException(e.getMessage());
        }
    }
    
}
