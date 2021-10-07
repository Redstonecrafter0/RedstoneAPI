package net.redstonecraft.redstoneapi.webserver.internal;

/**
 * RedstoneAPI
 *
 * @author Redstonecrafter0
 */
public class WebSocketPing {

    private long time;
    private long payload;

    public WebSocketPing(long time, long payload) {
        this.time = time;
        this.payload = payload;
    }

    public long getTime() {
        return time;
    }

    public long getPayload() {
        return payload;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setPayload(long payload) {
        this.payload = payload;
    }

}
