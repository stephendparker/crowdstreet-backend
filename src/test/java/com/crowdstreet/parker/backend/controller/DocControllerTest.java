package com.crowdstreet.parker.backend.controller;


import com.crowdstreet.parker.backend.IUniqueIdGenerator;
import com.crowdstreet.parker.backend.TestUtils;
import com.crowdstreet.parker.backend.data.DocPayload;
import com.crowdstreet.parker.backend.data.DocPayloadRepository;
import com.crowdstreet.parker.backend.thirdparty.IThirdPartyService;
import com.crowdstreet.parker.backend.thirdparty.ThirdPartyRequestException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.CleanupFailureDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocController.class)
public class DocControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocController docController;

    @MockBean
    private IThirdPartyService thirdPartyServiceMock;

    @MockBean
    private DocPayloadRepository docPayloadRepositoryMock;

    @MockBean
    private IUniqueIdGenerator idGeneratorMock;

    @Test
    public void testRequest() throws Exception {

        String id = "12345";
        String body = "body";

        when(idGeneratorMock.generateUniqueId()).thenReturn(id);
        DocPayloadRequestDto requestDto = new DocPayloadRequestDto();
        requestDto.body = body;

        // verify return is successful and id is returned
        this.mockMvc.perform(post("/request")
                .content(TestUtils.asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(id)));

        // verify id generator is called
        verify(idGeneratorMock, times(1)).generateUniqueId();

        // verify third party service is called
        verify(thirdPartyServiceMock, times(1)).requestDocPayload(argThat(inBody -> {
            assertEquals(body, inBody);
            return true;
        }), argThat(inCallback -> {
            assertThat(inCallback, containsString(DocController.CALLBACK_PATH + "/" + id));
            return true;
        }));

        // verify repository is called
        verify(docPayloadRepositoryMock, times(1)).save(argThat(inSave -> {
            assertEquals(inSave.body, body);
            assertEquals(inSave.id, id);
            assertEquals(inSave.status, DocPayload.INTIAL_STATUS);
            assertNull(inSave.detail);
            return true;
        }));
    }

    @Test
    public void testRequestThrowsThirdPartyErrorReturnCode() throws Exception {

        String id = "12345";
        String body = "body";
        String exceptionDescription = "Error calling third party service to initiate payload request";

        when(idGeneratorMock.generateUniqueId()).thenReturn(id);
        DocPayloadRequestDto requestDto = new DocPayloadRequestDto();
        requestDto.body = body;

        Mockito.doThrow(new ThirdPartyRequestException(exceptionDescription, new RuntimeException("Runtime"))).
                when(thirdPartyServiceMock).requestDocPayload(anyString(), anyString());

        this.mockMvc.perform(post("/request")
                .content(TestUtils.asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Unexpected error communicating with third party.")));
    }

    @Test
    public void testRequestThrowsDataException() throws Exception {

        String id = "12345";
        String body = "body";
        String exceptionDescription = "Error calling third party service to initiate payload request";

        when(idGeneratorMock.generateUniqueId()).thenReturn(id);
        DocPayloadRequestDto requestDto = new DocPayloadRequestDto();
        requestDto.body = body;

        Mockito.doThrow(new CleanupFailureDataAccessException("test", new RuntimeException("test"))).
                when(this.docPayloadRepositoryMock).save(any(DocPayload.class));

        this.mockMvc.perform(post("/request")
                .content(TestUtils.asJsonString(requestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Unexpected data exception.")));
    }

    @Test
    public void testPost() throws Exception {

        String id = "12345";
        String body = "body";
        String status = "STARTED";

        DocPayload databaseObj = new DocPayload();
        Optional<DocPayload> opt = Optional.of(databaseObj);
        databaseObj.id = id;
        databaseObj.body = body;

        when(docPayloadRepositoryMock.findById(anyString())).thenReturn(opt);

        // verify return is successful and id is returned
        this.mockMvc.perform(post("/" + DocController.CALLBACK_PATH + "/" + id)
                .content(status)
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)).andDo(print()).andExpect(status().isNoContent());

        // verify repository is called
        verify(docPayloadRepositoryMock, times(1)).save(argThat(inSave -> {
            assertEquals(id, inSave.id);
            assertEquals(body, inSave.body);
            assertEquals(status, inSave.status);
            return true;
        }));
    }

    @Test
    public void testPostNotFound() throws Exception {

        String id = "12345";
        String status = "STARTED";

        Optional<DocPayload> opt = Optional.empty();

        when(docPayloadRepositoryMock.findById(anyString())).thenReturn(opt);

        // verify return is successful
        this.mockMvc.perform(post("/" + DocController.CALLBACK_PATH + "/" + id)
                .content(status)
                .contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.TEXT_PLAIN)).andDo(print()).andExpect(status().isNotFound());

        // verify repository is called
        verify(docPayloadRepositoryMock, never()).save(ArgumentMatchers.any(DocPayload.class));
    }

    @Test
    public void testPut() throws Exception {

        String id = "12345";
        String body = "body";
        String status = "status";
        String detail = "detail";

        DocPayloadPutDto putDto = new DocPayloadPutDto();
        putDto.detail = detail;
        putDto.status = status;

        DocPayload existingObj = new DocPayload();
        Optional<DocPayload> existingOpt = Optional.of(existingObj);
        existingObj.id = id;
        existingObj.body = body;

        DocPayload databaseObj = new DocPayload();
        Optional<DocPayload> opt = Optional.of(databaseObj);
        databaseObj.id = id;
        databaseObj.body = body;
        databaseObj.status = status;
        databaseObj.detail = detail;

        when(docPayloadRepositoryMock.findById(anyString())).thenReturn(existingOpt);

        // verify return is successful
        this.mockMvc.perform(put("/" + DocController.CALLBACK_PATH + "/" + id)
                .content(TestUtils.asJsonString(putDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNoContent());

        // verify repository is called
        verify(docPayloadRepositoryMock, times(1)).save(argThat(inSave -> {
            assertEquals(id, inSave.id);
            assertEquals(body, inSave.body);
            assertEquals(status, inSave.status);
            assertEquals(detail, inSave.detail);
            return true;
        }));
    }

    @Test
    public void testPutNotFound() throws Exception {

        String id = "12345";
        String status = "status";
        String detail = "detail";

        DocPayloadPutDto putDto = new DocPayloadPutDto();
        putDto.detail = detail;
        putDto.status = status;

        Optional<DocPayload> opt = Optional.empty();

        when(docPayloadRepositoryMock.findById(anyString())).thenReturn(opt);

        // verify response returns not found code
        this.mockMvc.perform(put("/" + DocController.CALLBACK_PATH + "/" + id)
                .content(TestUtils.asJsonString(putDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound());

        // verify repository is never called
        verify(docPayloadRepositoryMock, never()).save(ArgumentMatchers.any(DocPayload.class));
    }

    @Test
    public void testGet() throws Exception {

        String id = "12345";
        String body = "body";
        String status = "status";
        String detail = "detail";

        DocPayload databaseObj = new DocPayload();
        Optional<DocPayload> opt = Optional.of(databaseObj);
        databaseObj.id = id;
        databaseObj.body = body;
        databaseObj.status = status;
        databaseObj.detail = detail;

        when(docPayloadRepositoryMock.findById(anyString())).thenReturn(opt);

        MvcResult result = this.mockMvc.perform(get("/status/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        DocPayloadStatusResponseDto responseDto = TestUtils.statusFromJson(content);

        assertEquals(responseDto.body, body);
        assertEquals(responseDto.status, status);
        assertEquals(responseDto.detail, detail);
    }

    @Test
    public void testGetNotFound() throws Exception {

        String id = "12345";

        Optional<DocPayload> opt = Optional.empty();

        when(docPayloadRepositoryMock.findById(anyString())).thenReturn(opt);

        this.mockMvc.perform(get("/status/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isNotFound());
    }
}