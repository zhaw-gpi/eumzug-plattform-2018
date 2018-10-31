package ch.zhaw.gpi.eumzugplattform.processdata;

/**
 * Klasse, welche die "komplexe" Antwort der checkBaseInsurance-Methode modelliert, also
 * ein Resultat mit den möglichen Werten "Yes", "No" oder "Unknown" sowie Details 
 * falls "No" oder "Unknown"
 * 
 * @author scep
 */
public class CheckBaseInsuranceResult {
    private String checkResult;
    private String checkResultDetails;

    public String getCheckResult() {
        return checkResult;
    }

    public CheckBaseInsuranceResult setCheckResult(String checkResult) {
        this.checkResult = checkResult;
        return this;
    }

    public String getCheckResultDetails() {
        return checkResultDetails;
    }

    public CheckBaseInsuranceResult setCheckResultDetails(String checkResultDetails) {
        this.checkResultDetails = checkResultDetails;
        return this;
    }
    
    
}