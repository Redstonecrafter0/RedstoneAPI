package net.redstonecraft.redstoneapi.discord.listeners;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.redstonecraft.redstoneapi.discord.abs.DiscordEvent;
import net.redstonecraft.redstoneapi.discord.abs.DiscordEventListener;
import net.redstonecraft.redstoneapi.discord.abs.SlashCommandManager;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public class SlashCommandListener implements DiscordEventListener {

    private final SlashCommandManager slashCommandManager;

    public SlashCommandListener(SlashCommandManager slashCommandManager) {
        this.slashCommandManager = slashCommandManager;
    }

    @DiscordEvent
    public void onSlashCommand(SlashCommandEvent event) {
        slashCommandManager.performCommand(event.getMember(), event.getTextChannel(), event, event.getName(), event.getSubcommandName(), event.getOptions());
    }

}
