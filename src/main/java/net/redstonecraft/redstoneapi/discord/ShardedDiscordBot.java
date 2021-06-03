package net.redstonecraft.redstoneapi.discord;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.redstonecraft.redstoneapi.discord.abs.CommandManager;
import net.redstonecraft.redstoneapi.discord.listeners.CommandListener;
import net.redstonecraft.redstoneapi.discord.managers.EventManager;

import javax.security.auth.login.LoginException;

/**
 * A auto sharded discordbot wrapper containing a commandmanager and simpler event listener
 *
 * @author Redstonecrafter0
 * @since 1.4
 * */
public class ShardedDiscordBot<T extends CommandManager> {

    private final ShardManager shardManager;
    private final T commandManager;
    private final EventManager eventManager;
    private final String commandPrefix;

    public ShardedDiscordBot(String token, String commandPrefix, T commandManager) throws LoginException {
        shardManager = DefaultShardManagerBuilder.create(token, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS)).build();
        this.commandManager = commandManager;
        this.commandPrefix = commandPrefix;
        eventManager = new EventManager();
        shardManager.addEventListener(eventManager);
        eventManager.addEventListener(new CommandListener(this.commandPrefix, this.commandManager));
    }

    public T getCommandManager() {
        return commandManager;
    }

    public String getCommandPrefix() {
        return commandPrefix;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
