package net.redstonecraft.redstoneapi.terminal.abs;

public abstract class Command {

    private final String commandName;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public abstract void onCommand(String[] args);

    public String getCommandName() {
        return commandName;
    }

}
