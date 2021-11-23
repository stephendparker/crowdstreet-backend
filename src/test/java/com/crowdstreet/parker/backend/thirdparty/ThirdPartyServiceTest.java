package com.crowdstreet.parker.backend.thirdparty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ThirdPartyServiceTest {

    @Autowired
    private IThirdPartyService thirdPartyService;

    @MockBean
    RestTemplate restTemplate;

    @Value("${thirdpartyurl}")
    String thirdPartyServiceUrl;

    @Test
    public void testContextLoad() throws Exception {
        assertNotNull(thirdPartyService);
        assertNotNull(restTemplate);
    }

    @Test
    public void testPass() throws Exception {

        String body = "body content";
        String callBack = "callBack.com";

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        ThirdPartyRequestDto request = new ThirdPartyRequestDto(body, callBack);
        HttpEntity<ThirdPartyRequestDto> entity = new HttpEntity<ThirdPartyRequestDto>(request, headers);

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class))).
                thenReturn(new ResponseEntity<String>("hi", HttpStatus.OK));

        thirdPartyService.requestDocPayload(body, callBack);

        verify(restTemplate, times(1)).exchange((String) argThat(inUrl -> {
            assertEquals(inUrl, thirdPartyServiceUrl);
            return true;
        }), argThat(inHttpMethod -> {
            assertEquals(inHttpMethod, HttpMethod.POST);
            return true;
        }), argThat(inEntity -> {
            assertEquals(entity.getBody().body, body);
            assertTrue(entity.getHeaders().getAccept().contains(MediaType.APPLICATION_JSON));
            return true;
        }), (Class<String>) argThat(inClass -> {
            assertEquals(inClass, String.class);
            return true;
        }));
    }


    @Test()
    public void testException() throws Exception {

        String body = "body content";
        String callBack = "callBack.com";
        String exceptionDescription = "my exception";

        when(restTemplate.exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class),
                any(Class.class))).thenThrow(new RuntimeException(exceptionDescription));

        ThirdPartyRequestException thrown = Assertions.assertThrows(ThirdPartyRequestException.class, () -> {
            thirdPartyService.requestDocPayload(body, callBack);
        });

        assertEquals(thrown.getMessage(), "Error calling third party service to initiate payload request");
    }
}
