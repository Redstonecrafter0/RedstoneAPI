package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.core.HttpHeader;
import net.redstonecraft.redstoneapi.core.HttpResponseCode;

public class ErrorResponse extends WebResponse {

    private HttpResponseCode errorCode;

    public ErrorResponse(HttpResponseCode errorCode, String content, HttpHeader... headers) {
        super(content, headers);
        this.errorCode = errorCode;
    }

    public ErrorResponse(HttpResponseCode errorCode, byte[] content, HttpHeader... headers) {
        super(content, headers);
        this.errorCode = errorCode;
    }

    public void setErrorCode(HttpResponseCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public HttpResponseCode getCode() {
        return errorCode;
    }

}
