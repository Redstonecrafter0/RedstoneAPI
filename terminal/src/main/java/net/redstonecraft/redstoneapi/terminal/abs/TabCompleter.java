package net.redstonecraft.redstoneapi.terminal.abs;

public interface TabCompleter {

    Iterable<String> onTabComplete(String[] args);

}
