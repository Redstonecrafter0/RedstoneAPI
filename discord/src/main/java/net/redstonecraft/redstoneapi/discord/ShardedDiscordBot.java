package net.redstonecraft.redstoneapi.discord;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.redstonecraft.redstoneapi.discord.abs.CommandManager;
import net.redstonecraft.redstoneapi.discord.abs.SlashCommandManager;
import net.redstonecraft.redstoneapi.discord.listeners.CommandListener;
import net.redstonecraft.redstoneapi.discord.listeners.SlashCommandListener;
import net.redstonecraft.redstoneapi.discord.managers.ButtonManager;
import net.redstonecraft.redstoneapi.discord.managers.EventManager;

import javax.security.auth.login.LoginException;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * A auto sharded discordbot wrapper containing two commandmanagers and simpler event listener
 *
 * @author Redstonecrafter0
 * @since 1.4
 * */
public class ShardedDiscordBot<C extends CommandManager, S extends SlashCommandManager> extends AbstractDiscordBot<ShardManager, C, S> {

    private final ShardManager shardManager;
    private final C commandManager;
    private final S slashCommandManager;
    private final EventManager eventManager;
    private final ButtonManager buttonManager;
    private final int buttonLifetime;
    private final String commandPrefix;

    public ShardedDiscordBot(String token, S slashCommandManager) throws LoginException {
        this(token, null, null, slashCommandManager, 60, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), (i -> {}));
    }

    public ShardedDiscordBot(String token, String commandPrefix, C commandManager) throws LoginException {
        this(token, commandPrefix, commandManager, null, 60, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), (i -> {}));
    }

    public ShardedDiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager) throws LoginException {
        this(token, commandPrefix, commandManager, slashCommandManager, 60, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), (i -> {}));
    }

    public ShardedDiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager, int buttonLifetimeSeconds) throws LoginException {
        this(token, commandPrefix, commandManager, slashCommandManager, buttonLifetimeSeconds, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), (i -> {}));
    }

    public ShardedDiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager, int buttonLifetimeSeconds, Consumer<DefaultShardManagerBuilder> preBuild) throws LoginException {
        this(token, commandPrefix, commandManager, slashCommandManager, buttonLifetimeSeconds, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), preBuild);
    }

    public ShardedDiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager, int buttonLifetimeSeconds, Collection<GatewayIntent> intents) throws LoginException {
        this(token, commandPrefix, commandManager, slashCommandManager, buttonLifetimeSeconds, intents, (i -> {}));
    }

    public ShardedDiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager, int buttonLifetimeSeconds, Collection<GatewayIntent> intents, Consumer<DefaultShardManagerBuilder> preBuild) throws LoginException {
        DefaultShardManagerBuilder shardManagerBuilder = DefaultShardManagerBuilder.create(token, intents);
        preBuild.accept(shardManagerBuilder);
        shardManager = shardManagerBuilder.build();
        this.commandManager = commandManager;
        this.slashCommandManager = slashCommandManager;
        this.commandPrefix = commandPrefix;
        this.buttonLifetime = buttonLifetimeSeconds;
        this.buttonManager = new ButtonManager(this.buttonLifetime);
        eventManager = new EventManager();
        shardManager.addEventListener(eventManager);
        if (commandManager != null) {
            eventManager.addEventListener(new CommandListener(this.commandPrefix, this.commandManager));
        }
        if (slashCommandManager != null) {
            eventManager.addEventListener(new SlashCommandListener(this.slashCommandManager));
        }
        eventManager.addEventListener(this.buttonManager);
    }

    @Override
    public C getCommandManager() {
        return commandManager;
    }

    @Override
    public S getSlashCommandManager() {
        return slashCommandManager;
    }

    @Override
    public ButtonManager getButtonManager() {
        return buttonManager;
    }

    @Override
    public void submitSlashCommands() {
        shardManager.getShards().forEach(jda -> {
            CommandListUpdateAction commands = jda.updateCommands();
            if (slashCommandManager != null) {
                commands.addCommands(slashCommandManager.getJdaCommands());
            }
            commands.queue();
        });
    }

    @Override
    public void submitSlashCommandsForGuild(Guild guild) {
        CommandListUpdateAction commands = guild.updateCommands();
        if (slashCommandManager != null) {
            commands.addCommands(slashCommandManager.getJdaCommands());
        }
        commands.queue();
    }

    @Override
    public String getCommandPrefix() {
        return commandPrefix;
    }

    @Override
    public int getButtonLifetime() {
        return buttonLifetime;
    }

    @Override
    public ShardManager getManager() {
        return shardManager;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

}
