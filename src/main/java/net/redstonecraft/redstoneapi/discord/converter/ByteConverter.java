package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class ByteConverter extends Converter<Byte> {

    public ByteConverter() {
        super(Byte.class);
    }

    @Override
    public Byte convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return Byte.parseByte(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

    @Override
    public Byte convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return Byte.parseByte(from);
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }
}
