package net.redstonecraft.redstoneapi.webserver.obj;

import net.redstonecraft.redstoneapi.tools.HttpHeader;
import net.redstonecraft.redstoneapi.tools.HttpResponseCode;

public class ErrorResponse extends WebResponse {

    private HttpResponseCode errorCode;

    public ErrorResponse(String content, HttpHeader... headers) {
        super(content, headers);
    }

    public ErrorResponse(byte[] content, HttpHeader... headers) {
        super(content, headers);
    }

    public void setErrorCode(HttpResponseCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public HttpResponseCode getCode() {
        return errorCode;
    }
}
