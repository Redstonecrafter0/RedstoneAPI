package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class FloatConverter extends Converter<Float> {

    public FloatConverter() {
        super(Float.class);
    }

    @Override
    public Float convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return Float.parseFloat(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

    @Override
    public Float convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return Float.parseFloat(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }
}
