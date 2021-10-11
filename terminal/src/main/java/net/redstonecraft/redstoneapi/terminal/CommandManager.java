package net.redstonecraft.redstoneapi.terminal;

import net.redstonecraft.redstoneapi.core.Pair;
import net.redstonecraft.redstoneapi.core.StringUtils;
import net.redstonecraft.redstoneapi.terminal.abs.Command;
import net.redstonecraft.redstoneapi.terminal.abs.TabCompleter;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandManager implements Completer {

    private final Map<String, Command> commandMap = new TreeMap<>();
    private Consumer<Pair<String, String[]>> commandNotFoundHandler = command -> System.err.println("Command " + command.first().toLowerCase() + " not found.");

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        String[] orgArgs = StringUtils.parseArgs(parsedLine.line());
        if (orgArgs.length == 0) {
            list.addAll(commandMap.keySet().stream().map(Candidate::new).collect(Collectors.toList()));
            return;
        }
        String commandS = orgArgs[0];
        String[] args = orgArgs.length == 1 ? new String[0] : Arrays.copyOfRange(orgArgs, 1, orgArgs.length);
        try {
            Command command = commandMap.get(commandS.toLowerCase());
            if (command instanceof TabCompleter tabCompleter) {
                for (String i : tabCompleter.onTabComplete(args)) {
                    list.add(new Candidate(i));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void setCommandNotFoundHandler(Consumer<Pair<String, String[]>> commandNotFoundHandler) {
        this.commandNotFoundHandler = commandNotFoundHandler;
    }

    public void registerCommand(Command command) {
        commandMap.put(command.getCommandName(), command);
    }

    public void removeCommand(String string) {
        commandMap.remove(string);
    }

    public void removeCommand(Command command) {
        removeCommand(command.getCommandName());
    }

    public void dispatchCommand(String command) {
        String[] orgArgs = StringUtils.parseArgs(command);
        if (orgArgs.length == 0) {
            return;
        }
        String commandS = orgArgs[0];
        String[] args = orgArgs.length == 1 ? new String[0] : Arrays.copyOfRange(orgArgs, 1, orgArgs.length);
        try {
            Command commandObject = commandMap.get(commandS.toLowerCase());
            if (commandObject != null) {
                commandObject.onCommand(args);
            } else {
                commandNotFoundHandler.accept(new Pair<>(command, args));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
