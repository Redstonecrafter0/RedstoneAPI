package net.redstonecraft.redstoneapi.terminal.internal;

import net.redstonecraft.redstoneapi.terminal.CommandManager;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.UserInterruptException;

public class TerminalHandler {

    private final String prompt;
    private final CommandManager commandManager;
    private final LineReader lineReader;

    private boolean run = true;
    private final Thread thread = new Thread(() -> {
        while (run) {
            tick();
        }
    });

    public TerminalHandler(String prompt, CommandManager commandManager, LineReader lineReader) {
        this.prompt = prompt;
        this.commandManager = commandManager;
        this.lineReader = lineReader;
        thread.start();
    }

    private void tick() {
        try {
            String[] lines = lineReader.readLine(prompt).split("\n");
            for (String i : lines) {
                if (i.length() > 0) {
                    commandManager.dispatchCommand(i);
                }
            }
        } catch (UserInterruptException | EndOfFileException ignored) {
        }
    }

    public void stop() {
        run = false;
    }

}
