package net.redstonecraft.redstoneapi.discord.obj;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * This is the first parameter for private message discord commands
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class PrivateContext {

    private final PrivateChannel channel;
    private final Message message;
    private final User user;

    public PrivateContext(PrivateChannel channel, Message message, User user) {
        this.channel = channel;
        this.message = message;
        this.user = user;
    }

    /**
     * Get the message object
     *
     * @return the message containing the command that was executed
     * */
    public Message getMessage() {
        return message;
    }

    /**
     * @return the privatechannel where the command was executed in
     * */
    public PrivateChannel getChannel() {
        return channel;
    }

    /**
     * @return the user that executed the command
     * */
    public User getUser() {
        return user;
    }
}
