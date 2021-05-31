package net.redstonecraft.redstoneapi.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.redstonecraft.redstoneapi.discord.abs.CommandManager;
import net.redstonecraft.redstoneapi.discord.listeners.CommandListener;
import net.redstonecraft.redstoneapi.discord.managers.EventManager;

import javax.security.auth.login.LoginException;

/**
 * A discordbot wrapper containing a commandmanager and simpler event listener
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class DiscordBot<T extends CommandManager> {

    private final JDA jda;
    private final T commandManager;
    private final EventManager eventManager;
    private final String commandPrefix;

    public DiscordBot(String token, String commandPrefix, T commandManager) throws LoginException {
        jda = JDABuilder.create(token, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).build();
        this.commandManager = commandManager;
        this.commandPrefix = commandPrefix;
        eventManager = new EventManager();
        jda.addEventListener(eventManager);
        eventManager.addEventListener(new CommandListener(this.commandPrefix, this.commandManager));
    }

    public T getCommandManager() {
        return commandManager;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public JDA getJda() {
        return jda;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
