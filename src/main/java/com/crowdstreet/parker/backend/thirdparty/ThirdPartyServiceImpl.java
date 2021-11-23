package com.crowdstreet.parker.backend.thirdparty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

// see - https://www.tutorialspoint.com/spring_boot/spring_boot_rest_template.htm
@Service
public class ThirdPartyServiceImpl implements IThirdPartyService {

    public static final String ERROR_MESSAGE = "Error calling third party service to initiate payload request";

    Logger logger = LoggerFactory.getLogger(ThirdPartyServiceImpl.class);

    RestTemplate restTemplate;
    String thirdPartyServiceUrl;

    @Autowired
    public ThirdPartyServiceImpl(RestTemplate restTemplate, @Value("${thirdpartyurl}") String thirdPartyServiceUrl) {

        this.restTemplate = restTemplate;
        this.thirdPartyServiceUrl = thirdPartyServiceUrl;
        logger.info("thirdPartyServiceUrl: " + thirdPartyServiceUrl);
    }

    @Override
    public void requestDocPayload(String body, String callBack) throws ThirdPartyRequestException {

         try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            ThirdPartyRequestDto request = new ThirdPartyRequestDto(body, callBack);
            HttpEntity<ThirdPartyRequestDto> entity = new HttpEntity<ThirdPartyRequestDto>(request, headers);

            logger.debug("calling third party service with callback: " + callBack);
            String retVal = restTemplate.exchange(
                    thirdPartyServiceUrl, HttpMethod.POST, entity, String.class).getBody();

            // TODO - possibly add validation on response to make sure it was received

            logger.debug("Received response from third party: " + retVal);
        } catch (Exception e) {

            throw new ThirdPartyRequestException(ThirdPartyServiceImpl.ERROR_MESSAGE, e);
        }
    }
}
