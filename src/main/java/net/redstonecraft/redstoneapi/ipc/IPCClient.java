package net.redstonecraft.redstoneapi.ipc;

import net.redstonecraft.redstoneapi.ipc.exceptions.InvalidResponse;
import net.redstonecraft.redstoneapi.ipc.request.Request;
import net.redstonecraft.redstoneapi.ipc.response.Response;
import net.redstonecraft.redstoneapi.ipc.response.ResponseStatus;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.json.parser.JSONParser;
import net.redstonecraft.redstoneapi.json.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * The client that connects to an {@link IPCServer}
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class IPCClient {

    private final String host;
    private final int port;
    private final String token;

    /**
     * Constructor to create a {@link IPCClient}
     *
     * @param host hostname
     * @param port host port
     * @param token client token that needs to be whitelistet on the {@link IPCServer}
     * */
    public IPCClient(String host, int port, String token) {
        this.host = host;
        this.port = port;
        this.token = token;
    }

    /**
     * Make a request to a {@link IPCServer}
     *
     * @param request a request object
     *
     * @return the {@link Response} sent by the server
     * */
    public Response request(Request request) throws IOException, InvalidResponse {
        Socket socket = new Socket(this.host, this.port);
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            JSONObject requestObj = new JSONObject();
            requestObj.put("token", token);
            requestObj.put("packet", request.getPacketName());
            requestObj.put("payload", request.getPayload());
            out.write(requestObj.toJSONString().getBytes(StandardCharsets.UTF_8));
            out.write(0);
            out.flush();
            StringBuilder sb = new StringBuilder();
            int c;
            while ((c = in.read()) != -1) {
                sb.append((char) c);
            }
            JSONObject response = (JSONObject) new JSONParser().parse(sb.toString());
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            return new Response(ResponseStatus.valueOf(response.getString("status")), response.getObject("payload"));
        } catch (ParseException | IllegalArgumentException e) {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
            throw new InvalidResponse(e);
        }
    }

}
