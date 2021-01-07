package net.redstonecraft.redstoneapi.ipc;

import net.redstonecraft.redstoneapi.ipc.interfaces.ServerProcessor;
import net.redstonecraft.redstoneapi.ipc.response.Response;
import net.redstonecraft.redstoneapi.ipc.response.ResponseStatus;
import net.redstonecraft.redstoneapi.json.JSONObject;
import net.redstonecraft.redstoneapi.json.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class IPCServer {

    private final ServerSocket socket;
    private final Thread thread;
    private final String serverToken;
    private final List<String> whitelistedTokens;
    private HashMap<String, ServerProcessor> processors = new HashMap<>();

    public IPCServer(String host, int port, String serverToken) throws IOException {
        this.serverToken = serverToken;
        whitelistedTokens = new ArrayList<>();
        socket = new ServerSocket(port, 0, InetAddress.getByName(host));
        thread = new Thread(() -> {
            while (true) {
                try {
                    Socket sock = socket.accept();
                    new Thread(() -> {
                        try {
                            InputStream in = sock.getInputStream();
                            OutputStream out = sock.getOutputStream();
                            StringBuilder sb = new StringBuilder();
                            int c;
                            while ((c = in.read()) != -1) {
                                    sb.append((char) c);
                            }
                            JSONObject obj = Objects.requireNonNull(JSONParser.parseObject(sb.toString()));
                            String token = obj.getString("token");
                            String response;
                            if (whitelistedTokens.contains(token)) {
                                String packet = obj.getString("packet");
                                JSONObject payload = obj.getObject("payload");
                                response = processRequest(token, packet, payload).toJSONString();
                            } else {
                                JSONObject uResponse = new JSONObject();
                                uResponse.put("status", ResponseStatus.ERROR.name);
                                JSONObject epayload = new JSONObject();
                                uResponse.put("payload", epayload);
                                response = uResponse.toJSONString();
                            }
                            out.write(response.getBytes(StandardCharsets.UTF_8));
                            out.flush();
                            sock.close();
                        } catch (Exception ignored) {
                            try {
                                sock.close();
                            } catch (IOException ignored1) {
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addProcessor(ServerProcessor processor) {
        processors.put(processor.getPacketName(), processor);
    }

    public void removeProcessor(ServerProcessor processor) {
        processors.remove(processor.getPacketName(), processor);
    }

    public void whitelistAdd(String token) {
        whitelistedTokens.add(token);
    }

    public void whitelistRemove(String token) {
        whitelistedTokens.remove(token);
    }

    private JSONObject processRequest(String token, String packet, JSONObject payload) {
        JSONObject response = new JSONObject();
        response.put("token", serverToken);
        try {
            ServerProcessor processor = processors.get(packet);
            if (processor != null) {
                Response respObj = processor.onProcess(payload);
                response.put("status", respObj.responseStatus.name);
                response.put("payload", respObj.payload);
            } else {
                response.put("status", ResponseStatus.ERROR.name);
                JSONObject errorBody = new JSONObject();
                response.put("error", "notFound");
                response.put("payload", errorBody);
            }
        } catch (Exception e) {
            response.put("status", ResponseStatus.ERROR.name);
            JSONObject epayload = new JSONObject();
            JSONObject report = new JSONObject();
            report.put("error", "exception");
            report.put("exception", e.getClass().getName());
            report.put("exceptionMsg", e.getMessage());
            payload.put("report", report);
            response.put("payload", epayload);
        }
        return response;
    }

    public void stop() throws IOException {
        thread.stop();
        socket.close();
    }
}
