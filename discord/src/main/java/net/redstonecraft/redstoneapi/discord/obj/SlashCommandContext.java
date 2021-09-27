package net.redstonecraft.redstoneapi.discord.obj;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * This is the first parameter for discord server commands
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
public class SlashCommandContext {

    private final TextChannel channel;
    private final SlashCommandEvent slashCommandEvent;
    private final Guild guild;
    private final Member member;

    public SlashCommandContext(TextChannel channel, SlashCommandEvent slashCommandEvent, Guild guild, Member member) {
        this.channel = channel;
        this.slashCommandEvent = slashCommandEvent;
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

    public SlashCommandEvent getSlashCommandEvent() {
        return slashCommandEvent;
    }

}
