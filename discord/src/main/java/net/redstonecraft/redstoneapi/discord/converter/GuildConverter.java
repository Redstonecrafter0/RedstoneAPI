package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class GuildConverter extends Converter<Guild> {

    public GuildConverter() {
        super(Guild.class);
    }

    @Override
    public Guild convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            Guild guild1 = jda.getGuildById(from);
            if (guild1 != null) {
                return guild1;
            } else {
                throw new ConvertException();
            }
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

    @Override
    public Guild convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            Guild guild = jda.getGuildById(from);
            if (guild != null) {
                return guild;
            } else {
                throw new ConvertException();
            }
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }
}
