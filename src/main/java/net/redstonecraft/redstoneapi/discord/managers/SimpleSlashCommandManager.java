package net.redstonecraft.redstoneapi.discord.managers;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.redstonecraft.redstoneapi.discord.abs.SimpleSlashCommand;
import net.redstonecraft.redstoneapi.discord.abs.SimpleSlashCommandOption;
import net.redstonecraft.redstoneapi.discord.abs.SimpleSlashCommands;
import net.redstonecraft.redstoneapi.discord.abs.SlashCommandManager;
import net.redstonecraft.redstoneapi.discord.obj.SlashCommandContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public class SimpleSlashCommandManager extends SlashCommandManager {

    private final Map<String, SlashCommandBundle> commands = new HashMap<>();
    private final Map<Class, Function<OptionMapping, Object>> converter = new HashMap<>();
    private final Map<Class, OptionType> optionTypes = new HashMap<>();
    private final List<CommandData> jdaCommands = new ArrayList<>();

    public SimpleSlashCommandManager() {
        converter.put(String.class, OptionMapping::getAsString);
        converter.put(Boolean.class, OptionMapping::getAsBoolean);
        converter.put(GuildChannel.class, OptionMapping::getAsMessageChannel);
        converter.put(TextChannel.class, OptionMapping::getAsGuildChannel);
        converter.put(VoiceChannel.class, OptionMapping::getAsGuildChannel);
        converter.put(Category.class, OptionMapping::getAsGuildChannel);
        converter.put(Long.class, OptionMapping::getAsLong);
        converter.put(Integer.class, optionMapping -> (int) optionMapping.getAsLong());
        converter.put(Member.class, OptionMapping::getAsMember);
        converter.put(MessageChannel.class, OptionMapping::getAsMessageChannel);
        converter.put(Role.class, OptionMapping::getAsRole);
        converter.put(User.class, OptionMapping::getAsUser);

        optionTypes.put(String.class, OptionType.STRING);
        optionTypes.put(Integer.class, OptionType.INTEGER);
        optionTypes.put(Long.class, OptionType.INTEGER);
        optionTypes.put(Boolean.class, OptionType.BOOLEAN);
        optionTypes.put(User.class, OptionType.USER);
        optionTypes.put(Member.class, OptionType.USER);
        optionTypes.put(TextChannel.class, OptionType.CHANNEL);
        optionTypes.put(VoiceChannel.class, OptionType.CHANNEL);
        optionTypes.put(Category.class, OptionType.CHANNEL);
        optionTypes.put(GuildChannel.class, OptionType.CHANNEL);
        optionTypes.put(MessageChannel.class, OptionType.CHANNEL);
        optionTypes.put(Role.class, OptionType.ROLE);
    }

    @SuppressWarnings("CodeBlock2Expr")
    @Override
    public Set<Map.Entry<String, SlashCommand>> getCommands() {
        Map<String, SlashCommand> map = new HashMap<>();
        commands.forEach((k, v) -> {
            v.methods.forEach((k1, v1) -> {
                map.put(k, new SlashCommand(k, k1, v.description));
            });
        });
        return map.entrySet();
    }

    public static class SlashCommandBundle {

        private final Map<String, Method> methods;
        private final SimpleSlashCommands instance;
        private final String description;

        private SlashCommandBundle(Map<String, Method> methods, SimpleSlashCommands instance, String description) {
            this.methods = methods;
            this.instance = instance;
            this.description = description;
        }

        public Map<String, Method> getMethods() {
            return methods;
        }

        public SimpleSlashCommands getInstance() {
            return instance;
        }

        public String getDescription() {
            return description;
        }

    }

    @Override
    public Collection<CommandData> getJdaCommands() {
        return jdaCommands;
    }

    public void addCommand(String name, String description, SimpleSlashCommands simpleSlashCommands) {
        List<Method> methods = Arrays.stream(simpleSlashCommands.getClass().getMethods()).filter(
                i -> i.isAnnotationPresent(SimpleSlashCommand.class) &&
                        i.getParameters().length > 0 &&
                        i.getParameterTypes()[0].equals(SlashCommandContext.class) &&
                        (i.getParameters().length == 1 || Arrays.stream(i.getParameters()).skip(1).allMatch(j -> j.isAnnotationPresent(SimpleSlashCommandOption.class)))
        ).collect(Collectors.toList());
        if (methods.stream().noneMatch(i -> i.isAnnotationPresent(SimpleSlashCommand.class))) {
            return;
        }
        commands.put(name, new SlashCommandBundle(methods.stream().collect(Collectors.toMap(i -> i.getAnnotation(SimpleSlashCommand.class).name(), i -> i, (i, i1) -> i)), simpleSlashCommands, description));
        jdaCommands.add(
                methods.stream().anyMatch(i -> i.getAnnotation(SimpleSlashCommand.class).name().equals("")) && Arrays.stream(methods.stream().filter(i -> i.getAnnotation(SimpleSlashCommand.class).name().equals("")).findFirst().orElse(null).getParameters()).anyMatch(i -> i.isAnnotationPresent(SimpleSlashCommandOption.class)) ?
                        new CommandData(name, description).addOptions(
                                Arrays.stream(methods.stream().filter(i -> i.getAnnotation(SimpleSlashCommand.class).name().equals("")).findFirst().get().getParameters()).skip(1).map(i -> {
                                    OptionData optionData = new OptionData(getOptionType(i.getType()), i.getAnnotation(SimpleSlashCommandOption.class).name(), i.getAnnotation(SimpleSlashCommandOption.class).info(), !i.getAnnotation(SimpleSlashCommandOption.class).optional());
                                    if (getOptionType(i.getType()).canSupportChoices()) {
                                        if (getOptionType(i.getType()).equals(OptionType.STRING)) {
                                            optionData.addChoices(Arrays.stream(i.getAnnotation(SimpleSlashCommandOption.class).stringChoices()).map(j -> new Command.Choice(j, j)).collect(Collectors.toList()));
                                        } else {
                                            optionData.addChoices(Arrays.stream(i.getAnnotation(SimpleSlashCommandOption.class).intChoices()).mapToObj(j -> new Command.Choice(String.valueOf(j), j)).collect(Collectors.toList()));
                                        }
                                    }
                                    return optionData;
                                }).collect(Collectors.toList())
        ) : new CommandData(name, description).addSubcommands(methods.stream().filter(i -> !i.getAnnotation(SimpleSlashCommand.class).name().equals("")).map(i -> new SubcommandData(i.getAnnotation(SimpleSlashCommand.class).name(), i.getAnnotation(SimpleSlashCommand.class).info()).addOptions(
                Arrays.stream(i.getParameters()).skip(1).map(j -> {
                    OptionData optionData = new OptionData(getOptionType(j.getType()), j.getAnnotation(SimpleSlashCommandOption.class).name(), j.getAnnotation(SimpleSlashCommandOption.class).info(), !j.getAnnotation(SimpleSlashCommandOption.class).optional());
                    if (getOptionType(j.getType()).canSupportChoices()) {
                        if (getOptionType(j.getType()).equals(OptionType.STRING)) {
                            optionData.addChoices(Arrays.stream(j.getAnnotation(SimpleSlashCommandOption.class).stringChoices()).map(k -> new Command.Choice(k, k)).collect(Collectors.toList()));
                        } else {
                            optionData.addChoices(Arrays.stream(j.getAnnotation(SimpleSlashCommandOption.class).intChoices()).mapToObj(k -> new Command.Choice(String.valueOf(k), k)).collect(Collectors.toList()));
                        }
                    }
                    return optionData;
                }).collect(Collectors.toList())
        )).collect(Collectors.toList())));
    }

    private OptionType getOptionType(Class type) {
        return optionTypes.get(type) == null ? OptionType.UNKNOWN : optionTypes.get(type);
    }

    @Override
    public void performCommand(Member member, TextChannel channel, SlashCommandEvent event, String command, String subCommand, List<OptionMapping> options) {
        SlashCommandBundle commandBundle = commands.get(command);
        if (commandBundle != null) {
            Method method = commandBundle.methods.get(subCommand == null ? "" : subCommand);
            if (method != null) {
                List<Object> opt = new ArrayList<>();
                opt.add(new SlashCommandContext(channel, event, channel.getGuild(), member));
                for (Parameter i : getParameters(method)) {
                    opt.add(options.stream().filter(o -> o.getName().equals(i.getAnnotation(SimpleSlashCommandOption.class).name())).map(o -> converter.get(i.getType()).apply(o)).findFirst().orElse(null));
                }
                try {
                    method.setAccessible(true);
                    method.invoke(commandBundle.instance, opt.toArray(new Object[0]));
                } catch (Throwable e) {
                    commandBundle.instance.handleError((SlashCommandContext) opt.get(0), e);
                }
            }
        }
    }

    private static Parameter[] getParameters(Method method) {
        if (method.getParameters().length > 1) {
            return Arrays.copyOfRange(method.getParameters(), 1, method.getParameters().length);
        } else {
            return new Parameter[0];
        }
    }

}
