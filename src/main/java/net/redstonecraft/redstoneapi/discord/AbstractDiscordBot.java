package net.redstonecraft.redstoneapi.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.redstonecraft.redstoneapi.discord.abs.CommandManager;
import net.redstonecraft.redstoneapi.discord.abs.SlashCommandManager;
import net.redstonecraft.redstoneapi.discord.managers.ButtonManager;
import net.redstonecraft.redstoneapi.discord.managers.EventManager;

/**
 * A discordbot wrapper containing two commandmanagers and simpler event listener
 *
 * @author Redstonecrafter0
 * @since 1.4
 */
public abstract class AbstractDiscordBot<T, C extends CommandManager, S extends SlashCommandManager> {

    public abstract C getCommandManager();

    public abstract S getSlashCommandManager();

    public abstract ButtonManager getButtonManager();

    public abstract void submitSlashCommands();

    public abstract String getCommandPrefix();

    public abstract int getButtonLifetime();

    public abstract T getManager();

    public abstract EventManager getEventManager();

}
