package net.redstonecraft.redstoneapi.tools.commandframework;

/**
 * The abstract command class for a command like the ones in spigot
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class CommandBase {

    public abstract boolean onCommand(String[] args);

    public abstract String usage();

}
