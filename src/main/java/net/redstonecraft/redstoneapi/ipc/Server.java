package net.redstonecraft.redstoneapi.ipc;

import net.redstonecraft.redstoneapi.ipc.interfaces.ServerProcessor;
import net.redstonecraft.redstoneapi.ipc.options.ServerOptions;
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

public class Server {

    private final ServerSocket serverSocket;
    private final Thread thread;
    private HashMap<Object, ServerProcessor> processors = new HashMap<>();
    private final ServerOptions options;

    public Server(String host, int port, String target, ServerOptions options) throws IOException {
        serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
        this.options = options;
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
                                if (c == 0) {
                                    JSONObject obj = Objects.requireNonNull(JSONParser.parseObject(sb.toString()));
                                    String from = obj.getString("from");
                                    String to = obj.getString("to");
                                    String token = obj.getString("token");
                                    String packet = obj.getString("packet");
                                    JSONObject payload = obj.getObject("payload");
                                    processRequest(from, to, token, packet, payload);
                                } else {
                                    sb.append((char) c);
                                }
                            }
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

    private void processRequest(String from, String to, String token, String packet, JSONObject payload) {
    }

    public void stop() {
        thread.stop();
    }
}
