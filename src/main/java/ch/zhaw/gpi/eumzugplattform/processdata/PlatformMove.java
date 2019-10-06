package ch.zhaw.gpi.eumzugplattform.processdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * eCH 194 PlatformMoveOut-Nachricht gemockt
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlatformMove {
    String callbackUrl, businessKey, firstName, surName, newTown;

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getNewTown() {
        return newTown;
    }

    public void setNewTown(String newTown) {
        this.newTown = newTown;
    }

    
    
}