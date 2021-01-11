package net.redstonecraft.redstoneapi.ipc.exceptions;

import net.redstonecraft.redstoneapi.json.parser.ParseException;

public class InvalidResponse extends Exception {

    public final ParseException parseException;

    public InvalidResponse(ParseException e) {
        parseException = e;
    }
}
