package net.redstonecraft.redstoneapi.ipc;

import net.redstonecraft.redstoneapi.ipc.request.Request;
import net.redstonecraft.redstoneapi.ipc.response.Response;

public class IPCClient {

    private final String host;
    private final int port;
    private final String token;

    public IPCClient(String host, int port, String token) {
        this.host = host;
        this.port = port;
        this.token = token;
    }

    public Response request(Request request) {
        Response response = new Response();
        return response;
    }

}
