package com.crowdstreet.parker.backend;

import com.crowdstreet.parker.backend.controller.DocPayloadStatusResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

// https://stackoverflow.com/questions/17143116/integration-testing-posting-an-entire-object-to-spring-mvc-controller
public class TestUtils {

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final String jsonContent = mapper.writeValueAsString(obj);
            return jsonContent;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static DocPayloadStatusResponseDto statusFromJson(final String json) {

        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, DocPayloadStatusResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
