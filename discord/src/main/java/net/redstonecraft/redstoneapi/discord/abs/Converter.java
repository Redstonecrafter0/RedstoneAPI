package net.redstonecraft.redstoneapi.discord.abs;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.converter.ConvertException;

/**
 * The abstract Converter needed for the {@link SimpleCommands}
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public abstract class Converter<T> {

    private final Class<T> clazz;

    public Converter(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * This method is used when the command is a server command.
     *
     * @param from the single argument of the command
     * @param jda the jda instance
     * @param message the message object
     * @param channel the textchnnel the message was sent in
     * @param member the member that executed the command
     * @param guild the guild on that the command was executed
     *
     * @return converted value
     *
     * @throws ConvertException when it can't be converter
     * */
    public abstract T convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException;

    /**
     * This method is used when the command is a private command.
     *
     * @param from the single argument of the command
     * @param jda the jda instance
     * @param message the message object
     * @param channel the textchnnel the message was sent in
     * @param user the user that executed the command
     *
     * @return converted value
     *
     * @throws ConvertException when it can't be converter
     * */
    public abstract T convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException;

    /**
     * @return the class in which the converter converts to
     * */
    public Class<T> convertsTo() {
        return clazz;
    }

}
