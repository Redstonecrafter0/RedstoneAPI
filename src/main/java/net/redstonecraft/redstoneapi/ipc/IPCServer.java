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
import java.util.HashMap;
import java.util.Objects;

public class IPCServer {

    private final ServerSocket serverSocket;
    private final Thread thread;
    private HashMap<Object, ServerProcessor> processors = new HashMap<>();

    public IPCServer(String host, int port, String target) throws IOException {
        serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
        thread = new Thread(() -> {
            while (true) {
                try {
                    Socket sock = serverSocket.accept();
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
                            String from = obj.getString("from");
                            String to = obj.getString("to");
                            String token = obj.getString("token");
                            String packet = obj.getString("packet");
                            JSONObject payload = obj.getObject("payload");
                            String response = processRequest(from, to, token, packet, payload).toJSONString();
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

    private JSONObject processRequest(String from, String to, String token, String packet, JSONObject payload) {
        JSONObject response = new JSONObject();
        response.put("from", to);
        response.put("to", from);
        response.put("token", token);
        try {
            ServerProcessor processor = processors.get(packet);
            if (processor != null) {
                Response respObj = processor.onProcess();
                response.put("status", respObj.responseStatus.name);
                response.put("payload", respObj.payload);
            } else {
                response.put("status", ResponseStatus.ERROR.name);
                response.put("payload", new JSONObject());
            }
        } catch (Exception e) {
            response.put("status", ResponseStatus.ERROR.name);
            JSONObject epayload = new JSONObject();
            JSONObject report = new JSONObject();
            report.put("exception", e.getClass().getName());
            report.put("exceptionMsg", e.getMessage());
            payload.put("report", report);
            response.put("payload", epayload);
        }
        return response;
    }

    public void stop() throws IOException {
        thread.stop();
        serverSocket.close();
    }
}
