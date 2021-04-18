package net.redstonecraft.redstoneapi.discord.abs;

import net.dv8tion.jda.api.entities.*;

/**
 * CommandManager Base
 *
 * @author Redstonecrafter0
 * @since 1.1
 * */
public abstract class CommandManager {

    /**
     * This method will by called by the {@link net.redstonecraft.redstoneapi.discord.listeners.CommandListener} when a command was detected on a discord server
     *
     * @param command command name
     * @param content message content
     * @param channel the channel the message came from
     * @param member the message author
     * @param message the message that was sent
     * @param guild the guild where the message was sent on
     */
    public abstract void performServerCommand(String command, String content, TextChannel channel, Member member, Message message, Guild guild);

    /**
     * This method will by called by the {@link net.redstonecraft.redstoneapi.discord.listeners.CommandListener} when a command was detected on a private message that was received
     *
     * @param command command name
     * @param content message content
     * @param channel the channel the message came from
     * @param user the message author
     * @param message the message that was sent
     */
    public abstract void performPrivateCommand(String command, String content, PrivateChannel channel, User user, Message message);

}
