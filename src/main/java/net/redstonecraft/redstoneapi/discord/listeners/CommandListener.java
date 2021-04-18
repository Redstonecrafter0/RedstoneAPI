package net.redstonecraft.redstoneapi.discord.listeners;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.redstonecraft.redstoneapi.discord.abs.CommandManager;
import net.redstonecraft.redstoneapi.discord.abs.DiscordEvent;
import net.redstonecraft.redstoneapi.discord.abs.DiscordEventListener;

public class CommandListener extends DiscordEventListener {

    private final String commandPrefix;
    private final CommandManager commandManager;

    public CommandListener(String commandPrefix, CommandManager commandManager) {
        this.commandPrefix = commandPrefix;
        this.commandManager = commandManager;
    }

    @DiscordEvent
    public void onGuildMessage(GuildMessageReceivedEvent event)  {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getMessage().getContentDisplay().startsWith(commandPrefix)) {
            return;
        }
        TextChannel channel = event.getChannel();
        Member member = event.getMember();
        Message message = event.getMessage();
        String msg = message.getContentRaw();
        if (!msg.startsWith(commandPrefix + " ") && !(msg.equals(commandPrefix) || msg.equals(commandPrefix + " "))) {
            msg = commandPrefix + " " + msg.substring(commandPrefix.length());
        }
        String[] oriArgs = msg.split(" ");
        String command = null;
        if (oriArgs.length >= 1) {
            command = oriArgs[1];
        }
        String finalCommand = command;
        String finalMsg = msg;
        new Thread(() -> commandManager.performServerCommand(finalCommand, finalMsg, channel, member, message, event.getGuild())).start();
    }

    @DiscordEvent
    public void onPrivateMessage(PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        if (!event.getMessage().getContentDisplay().startsWith(commandPrefix)) {
            return;
        }
        PrivateChannel channel = event.getChannel();
        User member = event.getAuthor();
        Message message = event.getMessage();
        String msg = message.getContentRaw();
        if (!msg.startsWith(commandPrefix + " ") && !(msg.equals(commandPrefix) || msg.equals(commandPrefix + " "))) {
            msg = commandPrefix + " " + msg.substring(commandPrefix.length());
        }
        String[] oriArgs = msg.split(" ");
        String command = null;
        if (oriArgs.length >= 1) {
            command = oriArgs[1];
        }
        String finalCommand = command;
        String finalMsg = msg;
        new Thread(() -> commandManager.performPrivateCommand(finalCommand, finalMsg, channel, member, message)).start();
    }

}
