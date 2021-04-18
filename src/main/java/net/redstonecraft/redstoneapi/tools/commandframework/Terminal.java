package net.redstonecraft.redstoneapi.tools.commandframework;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A terminal containing a {@link CommandManager}.
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class Terminal<T extends CommandManager> {

    private final T commandManager;
    private boolean run = true;

    public Terminal(T commandManager) {
        this.commandManager = commandManager;
        new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (run) {
                try {
                    String text = reader.readLine();
                    if (run) {
                        String response = this.commandManager.performCommand(text);
                        if (response != null) {
                            System.out.println(response);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public T getCommandManager() {
        return commandManager;
    }

    public void stop() {
        run = false;
    }

}
