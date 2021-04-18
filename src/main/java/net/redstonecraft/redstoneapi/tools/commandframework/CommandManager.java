package net.redstonecraft.redstoneapi.tools.commandframework;

/**
 * The abstract command manager
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class CommandManager {

    public abstract String performCommand(String text);

    public abstract String help();

}
