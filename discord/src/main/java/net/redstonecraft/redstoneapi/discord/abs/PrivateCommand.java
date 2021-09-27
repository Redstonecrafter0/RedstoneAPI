package net.redstonecraft.redstoneapi.discord.abs;

import net.redstonecraft.redstoneapi.discord.obj.PrivateContext;

/**
 * The private command executor for the {@link net.redstonecraft.redstoneapi.discord.managers.DefaultCommandManager}
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class PrivateCommand extends Command {

    /**
     * This method is called when a private command gets executed
     *
     * @param context the context that contains everything needed to interact
     * @param args the arguments used in the command
     *
     * @return if the command was successfully executed
     * */
    public abstract boolean onCommand(PrivateContext context, String[] args);

}
