package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class IntegerConverter extends Converter<Integer> {

    public IntegerConverter() {
        super(Integer.class);
    }

    @Override
    public Integer convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return Integer.parseInt(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

    @Override
    public Integer convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return Integer.parseInt(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }
}
