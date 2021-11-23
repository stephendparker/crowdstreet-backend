package com.crowdstreet.parker.backend.thirdparty;

public class ThirdPartyRequestException extends Exception {

    public ThirdPartyRequestException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
