package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class TextChannelConverter extends Converter<TextChannel> {

    public TextChannelConverter() {
        super(TextChannel.class);
    }

    @Override
    public TextChannel convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return guild.getTextChannelById(from.split("<#")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                TextChannel role = guild.getTextChannelById(from);
                if (role != null) {
                    return role;
                } else {
                    throw new ConvertException();
                }
            } catch (Exception ignored1) {
                throw new ConvertException();
            }
        }
    }

    @Override
    public TextChannel convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return jda.getTextChannelById(from.split("<@#")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                TextChannel role = jda.getTextChannelById(from);
                if (role != null) {
                    return role;
                } else {
                    throw new ConvertException();
                }
            } catch (Exception ignored1) {
                throw new ConvertException();
            }
        }
    }
}
