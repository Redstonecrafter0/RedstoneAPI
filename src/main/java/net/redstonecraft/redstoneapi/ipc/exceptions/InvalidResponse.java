package net.redstonecraft.redstoneapi.ipc.exceptions;

public class InvalidResponse extends Exception {

    public final Exception exception;

    public InvalidResponse(Exception e) {
        exception = e;
    }
}
