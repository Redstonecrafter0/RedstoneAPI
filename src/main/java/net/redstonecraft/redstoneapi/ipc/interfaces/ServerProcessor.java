package net.redstonecraft.redstoneapi.ipc.interfaces;

import net.redstonecraft.redstoneapi.ipc.response.Response;

public interface ServerProcessor {

    public Response onProcess();

    public String getPacketName();

}
