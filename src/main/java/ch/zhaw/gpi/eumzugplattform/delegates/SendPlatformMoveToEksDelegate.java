package ch.zhaw.gpi.eumzugplattform.delegates;

import javax.inject.Named;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ch.zhaw.gpi.eumzugplattform.processdata.PlatformMove;

/**
 * SendRequestDelegate
 */
@Named("sendPlatformMoveToEksAdapter")
public class SendPlatformMoveToEksDelegate implements JavaDelegate {

    private static final String REST_ENDPOINT = "http://localhost:8065/eksapi/v1/platformmove/";
    private static final String CALLBACK_URL = "http://localhost:8081/umzugapi/v1/technicalreceipt/";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        PlatformMove platformMove = new PlatformMove();
        platformMove.setBusinessKey(execution.getBusinessKey());
        platformMove.setCallbackUrl(CALLBACK_URL);
        platformMove.setFirstName((String) execution.getVariable("firstName"));
        platformMove.setSurName((String) execution.getVariable("officialName"));
        platformMove.setNewTown((String) execution.getVariable("townMoveIn"));
        
        HttpEntity<PlatformMove> httpEntity = new HttpEntity<PlatformMove>(platformMove, headers);

        ResponseEntity<Object> responseEntity = new RestTemplate().postForEntity(REST_ENDPOINT, httpEntity, null);

        if(!responseEntity.getStatusCode().is2xxSuccessful()){
            throw new RuntimeException(responseEntity.getStatusCode().toString() + ": " + responseEntity.getBody());
        }
    }
}