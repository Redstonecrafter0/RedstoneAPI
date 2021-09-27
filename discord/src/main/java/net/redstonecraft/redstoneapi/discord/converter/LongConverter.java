package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class LongConverter extends Converter<Long> {

    public LongConverter() {
        super(Long.class);
    }

    @Override
    public Long convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return Long.parseLong(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

    @Override
    public Long convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return Long.parseLong(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }
}
