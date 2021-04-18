package net.redstonecraft.redstoneapi.discord.abs;

import net.redstonecraft.redstoneapi.discord.obj.ServerContext;

/**
 * The server command executor for the {@link net.redstonecraft.redstoneapi.discord.managers.DefaultCommandManager}
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class ServerCommand extends Command {

    /**
     * This method is called when a server command gets executed
     *
     * @param context the context that contains everything needed to interact
     * @param args the arguments used in the command
     *
     * @return if the command was successfully executed
     * */
    public abstract boolean onCommand(ServerContext context, String[] args);

}
