package com.crowdstreet.parker.backend.controller;

import com.crowdstreet.parker.backend.data.DocPayload;

public class DocPayloadStatusResponseDto {

    public String status;
    public String detail;
    public String body;

    public DocPayloadStatusResponseDto() { }

    public DocPayloadStatusResponseDto(String status) {
        this.status = status;
    }

    public DocPayloadStatusResponseDto(DocPayload payload) {
        this.status = payload.status;
        this.detail = payload.detail;
        this.body = payload.body;
    }
}
