package net.redstonecraft.redstoneapi.core.proxy;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * {@link java.net.Socket} proxy using the SOCKS5 protocol without authentication
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class SOCKS5Proxy extends Proxy {

    public SOCKS5Proxy(String host, int port) throws IOException {
        super(host, port);
    }

    @Override
    public void connect(String host, int port) throws IOException {
        getOutputStream().write(new byte[]{0x05, 0x01, 0x00});
        getOutputStream().flush();
        byte[] resp = new byte[2];
        getInputStream().read(resp);
        if (resp[0] != 0x05 || resp[1] != 0x00) {
            close();
            return;
        }
        InetAddress addr = Inet4Address.getByName(host);
        byte[] portArr = ByteBuffer.allocate(2).putShort((short) port).array();
        getOutputStream().write(new byte[]{0x05, 0x01, 0x00, 0x01, addr.getAddress()[0], addr.getAddress()[1], addr.getAddress()[2], addr.getAddress()[3], portArr[0], portArr[1]});
        getOutputStream().flush();
        resp = new byte[10];
        getInputStream().read(resp);
        if (resp[0] != 0x05 || resp[1] != 0x00 || resp[2] != 0x00 || resp[3] != 0x01 || resp[8] != portArr[0] || resp[9] != portArr[1]) {
            close();
        }
    }

}
