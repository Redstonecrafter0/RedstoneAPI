package net.redstonecraft.redstoneapi.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final ServerSocket serverSocket;
    private final Thread thread;
    private boolean run;

    public Server(String host, int port, String target) throws IOException {
        serverSocket = new ServerSocket(port, 0, InetAddress.getByName(host));
        thread = new Thread(() -> {
            while (run) {
                try {
                    Socket sock = serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() {
        thread.stop();
    }
}
