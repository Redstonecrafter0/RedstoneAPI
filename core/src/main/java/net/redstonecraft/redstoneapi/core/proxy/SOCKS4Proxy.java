package net.redstonecraft.redstoneapi.core.proxy;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * {@link java.net.Socket} proxy using the SOCKS4 protocol
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class SOCKS4Proxy extends Proxy {

    public SOCKS4Proxy(String host, int port) throws IOException {
        super(host, port);
    }

    @Override
    public void connect(String host, int port) throws IOException {
        InetAddress addr = Inet4Address.getByName(host);
        byte[] portArr = ByteBuffer.allocate(2).putShort((short) port).array();
        getOutputStream().write(new byte[]{0x04, 0x01, portArr[0], portArr[1], addr.getAddress()[0], addr.getAddress()[1], addr.getAddress()[2], addr.getAddress()[3], 0x00});
        getOutputStream().flush();
        byte[] resp = new byte[8];
        getInputStream().read(resp);
        if (resp[0] != 0x00 || resp[1] != 0x5A) {
            close();
        }
    }

}
