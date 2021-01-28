package net.redstonecraft.redstoneapi.ipc.request;

import net.redstonecraft.redstoneapi.json.JSONObject;

/**
 * A interface to create a request to the {@link net.redstonecraft.redstoneapi.ipc.IPCServer} used in {@link net.redstonecraft.redstoneapi.ipc.IPCClient#request(Request)}
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public interface Request {

    /**
     * The packet name to get the right {@link net.redstonecraft.redstoneapi.ipc.interfaces.ServerProcessor} to work
     *
     * @return packet name
     * */
    String getPacketName();

    /**
     * Returns the payload sent to the {@link net.redstonecraft.redstoneapi.ipc.IPCServer}
     *
     * @return the payload
     * */
    JSONObject getPayload();

}
