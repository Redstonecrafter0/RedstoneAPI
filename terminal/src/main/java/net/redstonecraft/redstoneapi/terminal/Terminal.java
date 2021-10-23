package net.redstonecraft.redstoneapi.terminal;

import net.redstonecraft.redstoneapi.core.tuple.Pair;
import net.redstonecraft.redstoneapi.terminal.internal.CustomPrintStream;
import net.redstonecraft.redstoneapi.terminal.internal.TerminalHandler;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class Terminal {

    private final org.jline.terminal.Terminal terminal = TerminalBuilder.builder().system(true).encoding(StandardCharsets.UTF_8).build();
    private final CommandManager commandManager = new CommandManager();
    private final LineReader lineReader = LineReaderBuilder.builder().terminal(terminal).completer(commandManager).build();
    private final TerminalHandler terminalHandler;
    private final PrintStream sout;
    private final PrintStream serr;

    private static Terminal INSTANCE;

    private Terminal(String prompt) throws IOException {
        terminalHandler = new TerminalHandler(prompt, commandManager, lineReader);
        sout = System.out;
        serr = System.err;
        System.setOut(new CustomPrintStream(sout, lineReader));
        System.setErr(new CustomPrintStream(serr, lineReader));
    }

    public void setCommandNotFoundHandler(Consumer<Pair<String, String[]>> commandNotFoundHandler) {
        commandManager.setCommandNotFoundHandler(commandNotFoundHandler);
    }

    public static boolean isRunning() {
        return INSTANCE != null;
    }

    public static void create(String prompt) throws IOException {
        if (!isRunning()) {
            INSTANCE = new Terminal(prompt);
        }
    }

    public static void stop() {
        System.setOut(INSTANCE.sout);
        System.setErr(INSTANCE.serr);
        try {
            INSTANCE.terminalHandler.stop();
            INSTANCE.terminal.close();
        } catch (IOException ignored) {
        }
    }

    public static Terminal getInstance() {
        return INSTANCE;
    }

}
