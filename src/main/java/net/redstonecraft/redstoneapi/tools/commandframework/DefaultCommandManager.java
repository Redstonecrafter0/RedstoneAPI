package net.redstonecraft.redstoneapi.tools.commandframework;

import net.redstonecraft.redstoneapi.tools.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A command manager for managing command like them from spigot.
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class DefaultCommandManager extends CommandManager {

    private final Map<String, CommandBase> commandsRegistry = new TreeMap<>();

    @Override
    public String performCommand(String text) {
        try {
            String command = text.split(" ")[0];
            String content;
            try {
                content = text.substring(command.length() + 1);
            } catch (StringIndexOutOfBoundsException ignored) {
                content = "";
            }
            if (commandsRegistry.containsKey(command)) {
                CommandBase commandBase = commandsRegistry.get(command);
                String[] args = StringUtils.parseArgs(content);
                try {
                    if (!(boolean) commandBase.onCommand(args)) {
                        return commandBase.usage();
                    } else {
                        return null;
                    }
                } catch (Throwable e) {
                    return StringUtils.stringFromError(e);
                }
            }
            return "Command not found";
        } catch (Throwable e) {
            return StringUtils.stringFromError(e);
        }
    }

    public void registerCommand(String commandName, CommandBase command) {
        commandsRegistry.put(commandName, command);
    }

    @Override
    public String help() {
        List<String> list = new ArrayList<>();
        int longestCommand = 0;
        for (String i : commandsRegistry.keySet()) {
            if (i.length() > longestCommand) {
                longestCommand = i.length();
            }
        }
        int finalLongestCommand = longestCommand;
        commandsRegistry.forEach((key, value) -> list.add(String.format("%-" + finalLongestCommand + "s: %s", key, value.usage())));
        return String.join("\n", list);
    }

}
