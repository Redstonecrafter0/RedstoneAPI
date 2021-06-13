package net.redstonecraft.redstoneapi.tools.commandframework;

import net.redstonecraft.redstoneapi.tools.StringUtils;
import net.redstonecraft.redstoneapi.tools.commandframework.converters.*;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A command manager for simple command managing like from the {@link net.redstonecraft.redstoneapi.discord.DiscordBot}.
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class SimpleCommandManager extends CommandManager {

    private final Map<Class, Converter> converters = new HashMap<>();
    private final Map<String, CommandBundle> commandsRegistry = new TreeMap<>();

    public SimpleCommandManager() {
        registerConverters(
                new BooleanConverter(),
                new ByteConverter(),
                new DoubleConverter(),
                new FloatConverter(),
                new IntegerConverter(),
                new LongConverter()
        );
    }

    public void registerCommands(Commands commands) {
        for (Method i : commands.getClass().getMethods()) {
            try {
                if (i.isAnnotationPresent(Command.class) && !Modifier.isStatic(i.getModifiers()) && i.getReturnType().equals(boolean.class)) {
                    boolean works = true;
                    for (Class j : i.getParameterTypes()) {
                        if (!j.equals(String.class) && !converters.containsKey(j) && !j.isArray()) {
                            works = false;
                            break;
                        }
                    }
                    if (works) {
                        commandsRegistry.put(i.getAnnotation(Command.class).name().equals("") ? i.getName() : i.getAnnotation(Command.class).name(), new CommandBundle(i, commands, i.getAnnotation(Command.class).usage()));
                    }
                }
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        }
    }

    public void registerConverter(Converter converter) {
        converters.put(converter.convertsTo(), converter);
    }

    public void registerConverters(Converter... converters) {
        for (Converter i : converters) {
            registerConverter(i);
        }
    }

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
                CommandBundle commandBundle = commandsRegistry.get(command);
                String[] oriArgs = StringUtils.parseArgs(content);
                Object[] args = new Object[commandBundle.method.getParameterTypes().length];
                if (oriArgs.length != args.length) {
                    return commandBundle.usage;
                }
                for (int i = 0; i < oriArgs.length; i++) {
                    if (commandBundle.method.getParameterTypes()[i].equals(String.class)) {
                        args[i] = oriArgs[i];
                    } else {
                        try {
                            if (converters.get(commandBundle.method.getParameterTypes()[i]) != null) {
                                try {
                                    args[i] = converters.get(commandBundle.method.getParameterTypes()[i]).convert(oriArgs[i]);
                                } catch (NullPointerException ignored) {
                                    return commandBundle.usage;
                                }
                            } else {
                                return commandBundle.usage;
                            }
                        } catch (ConvertException ignored) {
                            return commandBundle.usage;
                        }
                    }
                }
                if (Arrays.equals(commandBundle.method.getParameterTypes(), getClasses(args))) {
                    try {
                        commandBundle.method.setAccessible(true);
                        if (!(boolean) commandBundle.method.invoke(commandBundle.simpleCommands, args)) {
                            return commandBundle.usage;
                        } else {
                            return null;
                        }
                    } catch (Throwable e) {
                        return StringUtils.stringFromError(e);
                    }
                } else {
                    return commandBundle.usage;
                }
            }
            return "Command not found";
        } catch (Throwable e) {
            return StringUtils.stringFromError(e);
        }
    }

    private static Class[] getClasses(Object[] obj) {
        Class[] arr = new Class[obj.length];
        for (int i = 0; i < arr.length; i++) {
            if (obj[i] == null) {
                return null;
            }
            arr[i] = obj[i].getClass();
        }
        return arr;
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
        commandsRegistry.forEach((key, value) -> list.add(String.format("%-" + finalLongestCommand + "s: %s", key, value.method.getAnnotation(Command.class).usage())));
        return String.join("\n", list);
    }

    private static class CommandBundle {

        private final Method method;
        private final Commands simpleCommands;
        private final String usage;

        private CommandBundle(Method method, Commands simpleCommands, String usage) {
            this.method = method;
            this.simpleCommands = simpleCommands;
            this.usage = usage;
        }

    }
}
