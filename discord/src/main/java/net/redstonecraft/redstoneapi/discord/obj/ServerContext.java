package net.redstonecraft.redstoneapi.discord.obj;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * This is the first parameter for discord server commands
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class ServerContext {

    private final TextChannel channel;
    private final Message message;
    private final Guild guild;
    private final Member member;

    public ServerContext(TextChannel channel, Message message, Guild guild, Member member) {
        this.channel = channel;
        this.message = message;
        this.guild = guild;
        this.member = member;
    }

    /**
     * @return the guild where the command was executed on
     * */
    public Guild getGuild() {
        return guild;
    }

    /**
     * @return the message that contains the command that was executed
     * */
    public Message getMessage() {
        return message;
    }

    /**
     * @return the channel where the command was executed in
     * */
    public TextChannel getChannel() {
        return channel;
    }

    /**
     * @return the member that executed the command
     * */
    public Member getMember() {
        return member;
    }

}
