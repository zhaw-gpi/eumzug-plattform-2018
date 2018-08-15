package ch.zhaw.gpi.eumzugplattform.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

/**
 * Generiert eine neue lokale PersonenId Als Id wird die AHV-Nummer gewählt,
 * sofern vorhanden, ansonsten ein Zusammenzug aus je 3 Buchstaben Nachnamen und
 * Vornamen sowie Geburtsdatum und Geschlecht, also z.B. aus Ruth Müller,
 * Weiblich (2), 1.1.1980 wird "RutMül_1980-01-01_2".
 */
@Service
public class LocalPersonIdGeneratorService {
    
    public String generateId(Long vn, String firstName, String officialName, Date dateOfBirth, String sex) {
        // Wenn die AHV-Nummer gesetzt ist und auch grösser als 0 ist, dann
        // soll diese als localPersonId verwendet werden
        if (vn != null && vn > 0) {
            return vn.toString();
        }

        // Ansonsten soll die localPersonId als Zusammenzug von 3 Buchstaben des Vornamens,
        // des Nachnamens, dem Geburtsdatum im Format JJJJ-MM-TT sowie dem
        // Geschlecht gebildet werden
        String firstNameShort = firstName.substring(0, 3);
        String officialNameShort = officialName.substring(0, 3);
        String formattedDateOfBirth = new SimpleDateFormat("YYYY-MM-DD").format(dateOfBirth);
        return firstNameShort + officialNameShort + "_" + formattedDateOfBirth + "_" + sex;
  }
}
