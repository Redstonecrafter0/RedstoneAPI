package net.redstonecraft.redstoneapi.discord.managers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.CommandManager;
import net.redstonecraft.redstoneapi.discord.abs.PrivateCommand;
import net.redstonecraft.redstoneapi.discord.abs.ServerCommand;
import net.redstonecraft.redstoneapi.discord.obj.PrivateContext;
import net.redstonecraft.redstoneapi.discord.obj.ServerContext;
import net.redstonecraft.redstoneapi.core.utils.StringUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A commandmanager for the {@link net.redstonecraft.redstoneapi.discord.DiscordBot} for commands like them from spigot
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class DefaultCommandManager extends CommandManager<ServerCommand, PrivateCommand> {

    private final Map<String, ServerCommand> serverCommands = new HashMap<>();
    private final Map<String, PrivateCommand> privateCommands = new HashMap<>();
    private final String title;

    public DefaultCommandManager(String title) {
        this.title = title;
    }

    public void registerServerCommand(String name, ServerCommand command) {
        serverCommands.put(name, command);
    }

    public void registerPrivateCommand(String name, PrivateCommand command) {
        privateCommands.put(name, command);
    }

    @Override
    public void performServerCommand(String command, String content, TextChannel channel, Member member, Message message, Guild guild) {
        if (serverCommands.containsKey(command)) {
            String[] oriArgs = StringUtils.parseArgs(content);
            String[] args = new String[oriArgs.length - 2];
            System.arraycopy(oriArgs, 2, args, 0, args.length);
            if (!serverCommands.get(command).onCommand(new ServerContext(channel, message, guild, member), args)) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(title);
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("```diff\n- " + serverCommands.get(command).usage() + "```");
                channel.sendMessage(eb.build()).queue();
            }
        }
    }

    @Override
    public void performPrivateCommand(String command, String content, PrivateChannel channel, User user, Message message) {
        if (privateCommands.containsKey(command)) {
            String[] oriArgs = StringUtils.parseArgs(content);
            String[] args = new String[oriArgs.length - 2];
            System.arraycopy(oriArgs, 2, args, 0, args.length);
            if (!privateCommands.get(command).onCommand(new PrivateContext(channel, message, user), args)) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(title);
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("```diff\n- " + privateCommands.get(command).usage() + "```");
                channel.sendMessage(eb.build()).queue();
            }
        }
    }

    @Override
    public Set<Map.Entry<String, PrivateCommand>> getPrivateCommands() {
        return privateCommands.entrySet();
    }

    @Override
    public Set<Map.Entry<String, ServerCommand>> getServerCommands() {
        return serverCommands.entrySet();
    }

}
