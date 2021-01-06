package net.redstonecraft.redstoneapi.ipc.response;

import net.redstonecraft.redstoneapi.json.JSONObject;

public class Response {

    public final ResponseStatus responseStatus;
    public final JSONObject payload;

    public Response(ResponseStatus responseStatus, JSONObject payload) {
        this.responseStatus = responseStatus;
        this.payload = payload;
    }

}
