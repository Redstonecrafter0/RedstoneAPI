package net.redstonecraft.redstoneapi.ipc.exceptions;

import net.redstonecraft.redstoneapi.ipc.request.Request;

/**
 * An exception that can be risen on {@link net.redstonecraft.redstoneapi.ipc.IPCClient#request(Request)}
 *
 * @author Redstonecrafter0
 * @since 1.0
 * */
public class InvalidResponse extends Exception {

    public final Exception exception;

    /**
     * Basic contructor to provide the cause {@link Exception}
     *
     * @param e {@link Exception} that caused this exception
     * */
    public InvalidResponse(Exception e) {
        exception = e;
    }
}
