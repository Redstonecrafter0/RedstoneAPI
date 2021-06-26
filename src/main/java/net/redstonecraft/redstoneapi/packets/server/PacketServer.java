package net.redstonecraft.redstoneapi.packets.server;

import net.redstonecraft.redstoneapi.packets.abs.PacketIn;
import net.redstonecraft.redstoneapi.packets.util.PacketInputStream;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class can be used to create an server using packets.
 *
 * @author Redstonecrafter0
 * @since 1.5
 */
public class PacketServer {

    private long lastKeepAlive = System.currentTimeMillis();
    private long lastKeepAliveId = 0;
    private boolean run = true;
    private final Thread thread;
    private final long timeout;
    private final Map<SocketChannel, ServerConnection> connections = new LinkedHashMap();
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final Map<Integer, Class<? extends PacketIn>> packetMap = new HashMap<>();
    private final Selector selector;
    private final ServerSocketChannel serverSocket;

    public PacketServer(String host, int port) throws IOException {
        this(host, port, 30000);
    }

    public PacketServer(int port) throws IOException {
        this("", port, 30000);
    }

    public PacketServer(int port, long timeout) throws IOException {
        this("", port, timeout);
    }

    public PacketServer(String host, int port, long timeout) throws IOException {
        this.timeout = timeout;
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(host, port));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, serverSocket.validOps(), null);
        thread = new Thread(() -> {
            while (run) {
                try {
                    tick();
                } catch (ClosedSelectorException ignored) {
                } catch (OutOfMemoryError ignored) {
                    System.gc();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void registerPacketIn(Class<PacketIn> packet) {
        try {
            packetMap.put(packet.newInstance().getPacketId(), packet);
        } catch (InstantiationException | IllegalAccessException ignored) {
            throw new IllegalArgumentException("The packet needs a public constructor without parameters");
        }
    }

    public void stop() {
        run = false;
        try {
            serverSocket.close();
            selector.close();
            thread.stop();
        } catch (Throwable ignored) {
        }
    }

    private void tick() throws IOException {
        long time = System.currentTimeMillis();
        lastKeepAliveId++;
        if (time >= lastKeepAlive + (timeout / 5)) {
            lastKeepAlive = time;
            long t = time - timeout;
            connections.entrySet().stream().filter(e -> e.getValue().lastPing < t).forEach(e -> {
                try {
                    e.getValue().channel.close();
                } catch (IOException ignored) {
                }
                connections.remove(e.getKey());
            });
            //noinspection Java8MapForEach
            connections.entrySet().forEach(e -> {
                try {
                    e.getValue().ping(lastKeepAliveId);
                    e.getValue().payload = lastKeepAliveId;
                } catch (IOException ignored) {
                    try {
                        e.getValue().channel.close();
                    } catch (IOException ignored1) {
                    }
                    connections.remove(e.getKey());
                }
            });
        }
        time += timeout;
        time -= lastKeepAlive;
        selector.select(time);
        for (SelectionKey key : selector.selectedKeys()) {
            try {
                if (!key.isValid()) {
                    continue;
                }
                if (key.isAcceptable()) {
                    SocketChannel channel = serverSocket.accept();
                    if (channel != null) {
                        channel.configureBlocking(false);
                        connections.put(channel, new ServerConnection(this, channel, lastKeepAliveId));
                        channel.register(selector, channel.validOps(), null);
                    }
                } else if (key.isReadable()) {
                    ServerConnection serverConnection = connections.get((SocketChannel) key.channel());
                    ByteBuffer head = ByteBuffer.allocate(8);
                    serverConnection.channel.read(head);
                    int packetId = head.getInt();
                    ByteBuffer buf = ByteBuffer.allocate(head.getInt());
                    serverConnection.channel.read(buf);
                    threadPool.submit(() -> {
                        Class<? extends PacketIn> packetClass = packetMap.get(packetId);
                        if (packetClass == null) {
                            try {
                                serverConnection.channel.close();
                                connections.remove(serverConnection.channel);
                            } catch (IOException ignored) {
                            }
                        } else {
                            try {
                                PacketIn packet = packetClass.newInstance();
                                packet.read(new PacketInputStream(buf.array()));
                                packet.handle(serverConnection);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (IOException ignored) {
            }
        }
    }

}
