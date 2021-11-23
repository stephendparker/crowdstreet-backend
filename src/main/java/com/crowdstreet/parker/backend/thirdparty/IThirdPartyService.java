package com.crowdstreet.parker.backend.thirdparty;

public interface IThirdPartyService {

    void requestDocPayload(String body, String callBack) throws ThirdPartyRequestException;
}
