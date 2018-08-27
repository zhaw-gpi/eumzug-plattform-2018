package ch.zhaw.gpi.eumzugplattform.processdata;

/**
 * Klasse, welche die "komplexe" Antwort des REST-Services modelliert, also
 * ein Resultat mit den möglichen Werten "Yes", "No" oder "Unknown" sowie Details 
 * falls "No" oder "Unknown"
 * 
 * @author scep
 */
public class VeKaResponse {
    private String checkResult;
    private String checkResultDetails;

    public String getCheckResult() {
        return checkResult;
    }

    public VeKaResponse setCheckResult(String checkResult) {
        this.checkResult = checkResult;
        return this;
    }

    public String getCheckResultDetails() {
        return checkResultDetails;
    }

    public VeKaResponse setCheckResultDetails(String checkResultDetails) {
        this.checkResultDetails = checkResultDetails;
        return this;
    }
    
    
}
