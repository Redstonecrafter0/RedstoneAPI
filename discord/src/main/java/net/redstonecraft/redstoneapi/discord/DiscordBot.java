package net.redstonecraft.redstoneapi.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
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
 * A discordbot wrapper containing two commandmanagers and simpler event listener
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class DiscordBot<C extends CommandManager, S extends SlashCommandManager> extends AbstractDiscordBot<JDA, C, S> {

    private final JDA jda;
    private final C commandManager;
    private final S slashCommandManager;
    private final EventManager eventManager;
    private final ButtonManager buttonManager;
    private final int buttonLifetime;
    private final String commandPrefix;

    public DiscordBot(String token, S slashCommandManager) throws LoginException {
        this(token, null, null, slashCommandManager, 60, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), (i -> {}));
    }

    public DiscordBot(String token, String commandPrefix, C commandManager) throws LoginException {
        this(token, commandPrefix, commandManager, null, 60, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), (i -> {}));
    }

    public DiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager) throws LoginException {
        this(token, commandPrefix, commandManager, slashCommandManager, 60, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), (i -> {}));
    }

    public DiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager, int buttonLifetimeSeconds) throws LoginException {
        this(token, commandPrefix, commandManager, slashCommandManager, buttonLifetimeSeconds, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), (i -> {}));
    }

    public DiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager, int buttonLifetimeSeconds, Consumer<JDABuilder> preBuild) throws LoginException {
        this(token, commandPrefix, commandManager, slashCommandManager, buttonLifetimeSeconds, GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS), preBuild);
    }

    public DiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager, int buttonLifetimeSeconds, Collection<GatewayIntent> intents) throws LoginException {
        this(token, commandPrefix, commandManager, slashCommandManager, buttonLifetimeSeconds, intents, (i -> {}));
    }

    public DiscordBot(String token, String commandPrefix, C commandManager, S slashCommandManager, int buttonLifetimeSeconds, Collection<GatewayIntent> intents, Consumer<JDABuilder> preBuild) throws LoginException {
        JDABuilder jdaBuilder = JDABuilder.create(token, intents);
        preBuild.accept(jdaBuilder);
        jda = jdaBuilder.build();
        this.commandManager = commandManager;
        this.slashCommandManager = slashCommandManager;
        this.commandPrefix = commandPrefix;
        this.buttonLifetime = buttonLifetimeSeconds;
        this.buttonManager = new ButtonManager(this.buttonLifetime);
        eventManager = new EventManager();
        jda.addEventListener(eventManager);
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
        CommandListUpdateAction commands = jda.updateCommands();
        if (slashCommandManager != null) {
            commands.addCommands(slashCommandManager.getJdaCommands());
        }
        commands.queue();
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
    public JDA getManager() {
        return jda;
    }

    public JDA getJda() {
        return jda;
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

}
