/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.zhaw.gpi.eumzugplattform.delegates;

import ch.zhaw.gpi.eumzugplattform.processdata.VeKaResponse;
import ch.zhaw.gpi.eumzugplattform.services.VeKaClientService;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Named;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author scep
 */
@Named("checkBaseInsuranceAdapter")
public class CheckBaseInsuranceDelegate implements JavaDelegate {
    
    @Autowired
    private VeKaClientService veKaClientService;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
        Date dateOfBirth = (Date) delegateExecution.getVariable("dateOfBirth");
        String firstName = (String) delegateExecution.getVariable("firstName");
        String officialName = (String) delegateExecution.getVariable("officialName");
        Long baseInsuranceNumber = (Long) delegateExecution.getVariable("baseInsuranceNumber");
        
        VeKaResponse veKaResponse = veKaClientService.checkBaseInsurance(baseInsuranceNumber, firstName, officialName, dateOfBirth);
        
        delegateExecution.setVariable("checkBaseInsuranceResult", veKaResponse.getCheckResult());
        delegateExecution.setVariable("checkBaseInsuranceResultDetails", veKaResponse.getCheckResultDetails());
    }
    
}
