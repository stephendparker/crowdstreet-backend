package com.crowdstreet.parker.backend.thirdparty;

public class ThirdPartyRequestDto {

    public String body;
    public String callback;

    public ThirdPartyRequestDto(String body, String callback) {
        this.body = body;
        this.callback = callback;
    }
}
