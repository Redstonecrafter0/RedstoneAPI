package net.redstonecraft.redstoneapi.discord.managers;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.DiscordBot;
import net.redstonecraft.redstoneapi.discord.abs.CommandManager;
import net.redstonecraft.redstoneapi.discord.abs.Converter;
import net.redstonecraft.redstoneapi.discord.abs.SimpleCommand;
import net.redstonecraft.redstoneapi.discord.abs.SimpleCommands;
import net.redstonecraft.redstoneapi.discord.converter.*;
import net.redstonecraft.redstoneapi.discord.obj.PrivateContext;
import net.redstonecraft.redstoneapi.discord.obj.ServerContext;
import net.redstonecraft.redstoneapi.tools.StringUtils;

import java.awt.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A simple command manager using annotations for declaring commands with automatic argument parsing
 *
 * @author Redstonecrafter0
 * @since 1.2
 * */
public class SimpleCommandManager extends CommandManager {

    private final String title;
    private final Map<Class, Converter> converters = new HashMap<>();
    private final Map<String, CommandBundle> serverCommands = new TreeMap<>();
    private final Map<String, CommandBundle> privateCommands = new TreeMap<>();

    public SimpleCommandManager(String title) {
        this.title = title;
        registerConverters(
                new BooleanConverter(),
                new ByteConverter(),
                new CategoryConverter(),
                new DoubleConverter(),
                new EmoteConverter(),
                new FloatConverter(),
                new GuildConverter(),
                new IntegerConverter(),
                new LongConverter(),
                new MemberConverter(),
                new RoleConverter(),
                new TextChannelConverter(),
                new UserConverter(),
                new VoiceChannelConverter());
    }

    public void registerCommands(SimpleCommands... simpleCommands) {
        for (SimpleCommands commands : simpleCommands) {
            for (Method i : commands.getClass().getMethods()) {
                try {
                    if (i.isAnnotationPresent(SimpleCommand.class) && !Modifier.isStatic(i.getModifiers()) && i.getReturnType().equals(boolean.class) && i.getParameterTypes()[0].equals(ServerContext.class)) {
                        boolean works = true;
                        for (Class j : getTypes(i.getParameterTypes())) {
                            if (!j.equals(String.class) && !converters.containsKey(j) && !j.isArray()) {
                                works = false;
                                break;
                            }
                        }
                        if (works) {
                            serverCommands.put(i.getAnnotation(SimpleCommand.class).name().equals("") ? i.getName() : i.getAnnotation(SimpleCommand.class).name(), new CommandBundle(i, commands, i.getAnnotation(SimpleCommand.class).usage(), i.getAnnotation(SimpleCommand.class).info(), i.getAnnotation(SimpleCommand.class).permission()));
                        }
                    } else if (i.isAnnotationPresent(SimpleCommand.class) && !Modifier.isStatic(i.getModifiers()) && i.getReturnType().equals(boolean.class) && i.getParameterTypes()[0].equals(PrivateContext.class)) {
                        boolean works = true;
                        for (Class j : getTypes(i.getParameterTypes())) {
                            if (!j.equals(String.class) && !converters.containsKey(j) && !j.isArray()) {
                                works = false;
                                break;
                            }
                        }
                        if (works) {
                            privateCommands.put(i.getAnnotation(SimpleCommand.class).name().equals("") ? i.getName() : i.getAnnotation(SimpleCommand.class).name(), new CommandBundle(i, commands, i.getAnnotation(SimpleCommand.class).usage(), i.getAnnotation(SimpleCommand.class).info(), i.getAnnotation(SimpleCommand.class).permission()));
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
            }
        }
    }

    private void registerConverter(Converter converter) {
        converters.put(converter.convertsTo(), converter);
    }

    /**
     * Register custom converters for argument parsing
     * There are some preregistered converters
     *
     * @param converters the converters to register
     * */
    public void registerConverters(Converter... converters) {
        for (Converter i : converters) {
            registerConverter(i);
        }
    }

    @Override
    public void performServerCommand(String command, String content, TextChannel channel, Member member, Message message, Guild guild) {
        if (serverCommands.containsKey(command)) {
            CommandBundle commandBundle = serverCommands.get(command);
            if (!commandBundle.permission.equals(Permission.UNKNOWN)) {
                if (!member.hasPermission(commandBundle.permission)) {
                    return;
                }
            }
            String[] oriArgs = StringUtils.parseArgs(content);
            Object[] args = new Object[commandBundle.method.getParameterTypes().length];
            if (oriArgs.length != args.length + 1) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(title);
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                channel.sendMessage(eb.build()).queue();
                return;
            }
            args[0] = new ServerContext(channel, message, guild, member);
            for (int i = 2; i < oriArgs.length; i++) {
                if (commandBundle.method.getParameterTypes()[i - 1].equals(String.class)) {
                    args[i - 1] = oriArgs[i];
                } else {
                    try {
                        if (converters.get(commandBundle.method.getParameterTypes()[i - 1]) != null) {
                            try {
                                args[i - 1] = converters.get(commandBundle.method.getParameterTypes()[i - 1]).convertServer(oriArgs[i], guild.getJDA(), message, channel, member, guild);
                            } catch (NullPointerException ignored) {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle(title);
                                eb.setColor(Color.decode("#FF0000"));
                                eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                                channel.sendMessage(eb.build()).queue();
                                return;
                            }
                        } else {
                            return;
                        }
                    } catch (ConvertException ignored) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle(title);
                        eb.setColor(Color.decode("#FF0000"));
                        eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                        channel.sendMessage(eb.build()).queue();
                        return;
                    }
                }
            }
            if (Arrays.equals(commandBundle.method.getParameterTypes(), getClasses(args))) {
                try {
                    commandBundle.method.setAccessible(true);
                    if (!(boolean) commandBundle.method.invoke(commandBundle.simpleCommands, args)) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle(title);
                        eb.setColor(Color.decode("#FF0000"));
                        eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                        channel.sendMessage(eb.build()).queue();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(title);
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                channel.sendMessage(eb.build()).queue();
            }
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
    public void performPrivateCommand(String command, String content, PrivateChannel channel, User user, Message message) {
        if (privateCommands.containsKey(command)) {
            CommandBundle commandBundle = privateCommands.get(command);
            String[] oriArgs = StringUtils.parseArgs(content);
            Object[] args = new Object[commandBundle.method.getParameterTypes().length];
            if (oriArgs.length != args.length + 1) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(title);
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                channel.sendMessage(eb.build()).queue();
                return;
            }
            args[0] = new PrivateContext(channel, message, user);
            for (int i = 2; i < oriArgs.length; i++) {
                if (commandBundle.method.getParameterTypes()[i - 1].equals(String.class)) {
                    args[i - 1] = oriArgs[i];
                } else {
                    try {
                        if (converters.get(commandBundle.method.getParameterTypes()[i - 1]) != null) {
                            try {
                                args[i - 1] = converters.get(commandBundle.method.getParameterTypes()[i - 1]).convertPrivate(oriArgs[i], channel.getJDA(), message, channel, user);
                            } catch (NullPointerException ignored) {
                                EmbedBuilder eb = new EmbedBuilder();
                                eb.setTitle(title);
                                eb.setColor(Color.decode("#FF0000"));
                                eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                                channel.sendMessage(eb.build()).queue();
                                return;
                            }
                        } else {
                            return;
                        }
                    } catch (ConvertException ignored) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle(title);
                        eb.setColor(Color.decode("#FF0000"));
                        eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                        channel.sendMessage(eb.build()).queue();
                        return;
                    }
                }
            }
            if (Arrays.equals(commandBundle.method.getParameterTypes(), getClasses(args))) {
                try {
                    commandBundle.method.setAccessible(true);
                    if (!(boolean) commandBundle.method.invoke(commandBundle.simpleCommands, args)) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle(title);
                        eb.setColor(Color.decode("#FF0000"));
                        eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                        channel.sendMessage(eb.build()).queue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(title);
                eb.setColor(Color.decode("#FF0000"));
                eb.setDescription("```diff\n- " + commandBundle.usage + "```");
                channel.sendMessage(eb.build()).queue();
            }
        }
    }

    private static Class[] getTypes(Class[] classes) {
        Class[] tmp = new Class[classes.length - 1];
        System.arraycopy(classes, 1, tmp, 0, classes.length - 1);
        return tmp;
    }

    public Map<String, CommandBundle> getServerCommands() {
        return serverCommands;
    }

    public Map<String, CommandBundle> getPrivateCommands() {
        return privateCommands;
    }

    private static class CommandBundle {

        private final Method method;
        private final SimpleCommands simpleCommands;
        private final String usage;
        private final String info;
        private final Permission permission;

        private CommandBundle(Method method, SimpleCommands simpleCommands, String usage, String info, Permission permission) {
            this.method = method;
            this.simpleCommands = simpleCommands;
            this.usage = usage;
            this.info = info;
            this.permission = permission;
        }

    }

    /**
     * This is a help command so you only have to register it and don't have to write a custom one.
     * It is filtering the commands by permission.
     * It is <b>NOT</b> added by default.
     * */
    public static class DefaultServerHelpCommand implements SimpleCommands {

        private final boolean fullWidth;
        private final int itemsPerPage;
        private final String title;
        private final Color color;
        private final String indexOutOfBoundsText;
        private final DiscordBot<SimpleCommandManager> bot;

        public DefaultServerHelpCommand(DiscordBot<SimpleCommandManager> bot, boolean fullWidth, int itemsPerPage, String title, Color color, String indexOutOfBoundsText) {
            this.fullWidth = fullWidth;
            this.itemsPerPage = itemsPerPage;
            this.title = title;
            this.color = color;
            this.indexOutOfBoundsText = indexOutOfBoundsText;
            this.bot = bot;
        }

        @SimpleCommand(usage = "help <page>", info = "Show this help")
        public boolean help(ServerContext ctx, Integer page) {
            String[] index = bot.getCommandManager().serverCommands.entrySet().stream().filter(command -> ctx.getMember().hasPermission(command.getValue().permission)).map(Map.Entry::getKey).toArray(String[]::new);
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(title);
            eb.setColor(color);
            if (((int) Math.ceil(index.length / (double) itemsPerPage) < (page)) || page < 1) {
                eb.setDescription(String.format(indexOutOfBoundsText, (int) Math.ceil(index.length / (double) itemsPerPage)));
            } else {
                for (int i = (itemsPerPage * (page - 1)); i < (itemsPerPage * (page)); i++) {
                    try {
                        CommandBundle bundle = bot.getCommandManager().serverCommands.get(index[i]);
                        eb.addField(index[i], bot.getCommandPrefix() + bundle.usage + "\n" + bundle.info, fullWidth);
                    } catch (IndexOutOfBoundsException ignored) {
                        break;
                    }
                }
            }
            ctx.getChannel().sendMessage(eb.build()).queue();
            return true;
        }

    }

    /**
     * This is a help command so you only have to register it and don't have to write a custom one.
     * It is <b>NOT</b> added by default.
     * */
    public static class DefaultPrivateHelpCommand implements SimpleCommands {

        private final boolean fullWidth;
        private final int itemsPerPage;
        private final String title;
        private final Color color;
        private final String indexOutOfBoundsText;
        private final DiscordBot<SimpleCommandManager> bot;

        public DefaultPrivateHelpCommand(DiscordBot<SimpleCommandManager> bot, boolean fullWidth, int itemsPerPage, String title, Color color, String indexOutOfBoundsText) {
            this.fullWidth = fullWidth;
            this.itemsPerPage = itemsPerPage;
            this.title = title;
            this.color = color;
            this.indexOutOfBoundsText = indexOutOfBoundsText;
            this.bot = bot;
        }

        @SimpleCommand(usage = "help <page>", info = "Show this help")
        public boolean help(PrivateContext ctx, Integer page) {
            String[] index = bot.getCommandManager().privateCommands.keySet().toArray(new String[0]);
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(title);
            eb.setColor(color);
            if (((int) Math.ceil(index.length / (double) itemsPerPage) < (page)) || page < 1) {
                eb.setDescription(String.format(indexOutOfBoundsText, (int) Math.ceil(index.length / (double) itemsPerPage)));
            } else {
                for (int i = (itemsPerPage * (page - 1)); i < (itemsPerPage * (page)); i++) {
                    try {
                        CommandBundle bundle = bot.getCommandManager().privateCommands.get(index[i]);
                        eb.addField(index[i], bot.getCommandPrefix() + bundle.usage + "\n" + bundle.info, fullWidth);
                    } catch (IndexOutOfBoundsException ignored) {
                        break;
                    }
                }
            }
            ctx.getChannel().sendMessage(eb.build()).queue();
            return true;
        }

    }

}