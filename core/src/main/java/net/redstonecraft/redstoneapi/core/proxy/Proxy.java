package net.redstonecraft.redstoneapi.core.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Abstract Proxy for {@link Socket}s
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class Proxy {

    private final Socket socket;

    public Proxy(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    public abstract void connect(String host, int port) throws IOException;

    public void close() throws IOException {
        socket.close();
    }

    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

}
