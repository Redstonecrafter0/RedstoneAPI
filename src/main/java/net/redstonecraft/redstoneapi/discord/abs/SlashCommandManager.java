package net.redstonecraft.redstoneapi.discord.abs;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public abstract class SlashCommandManager {

    public abstract Set<Map.Entry<String, SlashCommand>> getCommands();

    public abstract void performCommand(Member member, TextChannel channel, SlashCommandEvent event, String command, String subCommand, List<OptionMapping> options);

    public abstract Collection<CommandData> getJdaCommands();

    public static class SlashCommand {

        private final String name;
        private final String subCommandName;
        private final String description;

        public SlashCommand(String name, String subCommandName, String description) {
            this.name = name;
            this.subCommandName = subCommandName;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getSubCommandName() {
            return subCommandName;
        }

        public String getDescription() {
            return description;
        }

    }

    public static class Option {

        private final String name;

        public Option(String name) {
            this.name = name;
        }

    }

}
