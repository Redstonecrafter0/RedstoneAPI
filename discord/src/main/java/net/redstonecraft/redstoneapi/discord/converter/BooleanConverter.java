package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class BooleanConverter extends Converter<Boolean> {

    public BooleanConverter() {
        super(Boolean.class);
    }

    @Override
    public Boolean convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return Boolean.parseBoolean(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

    @Override
    public Boolean convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return Boolean.parseBoolean(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }
}
