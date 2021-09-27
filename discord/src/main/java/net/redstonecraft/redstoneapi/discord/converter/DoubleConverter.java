package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class DoubleConverter extends Converter<Double> {

    public DoubleConverter() {
        super(Double.class);
    }

    @Override
    public Double convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return Double.parseDouble(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

    @Override
    public Double convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return Double.parseDouble(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }
}
