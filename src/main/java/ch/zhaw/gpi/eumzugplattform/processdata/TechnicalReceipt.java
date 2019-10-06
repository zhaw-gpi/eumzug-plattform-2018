package ch.zhaw.gpi.eumzugplattform.processdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * eCH 0058 Fachliche Quittung
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TechnicalReceipt {
    String businessKey;
    Integer responseCode;

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    
}