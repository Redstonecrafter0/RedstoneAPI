package net.redstonecraft.redstoneapi.ipc.response;

import net.redstonecraft.redstoneapi.ipc.request.Request;
import net.redstonecraft.redstoneapi.json.JSONObject;

/**
 * The response object returned by {@link net.redstonecraft.redstoneapi.ipc.IPCClient#request(Request)}
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class Response {

    public final ResponseStatus responseStatus;
    public final JSONObject payload;

    /**
     * Contructor for the {@link Response}
     *
     * @param responseStatus the status of the response
     * @param payload the payload privided by {@link net.redstonecraft.redstoneapi.ipc.IPCServer} on response
     * */
    public Response(ResponseStatus responseStatus, JSONObject payload) {
        this.responseStatus = responseStatus;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Response{" +
                "responseStatus=" + responseStatus.name() +
                ", payload=" + payload.toJSONString() +
                '}';
    }
}
