package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class EmoteConverter extends Converter<Emote> {

    public EmoteConverter() {
        super(Emote.class);
    }

    @Override
    public Emote convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return guild.getEmoteById(from.split("<:")[1].split(":")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                Emote role = guild.getEmoteById(from);
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
    public Emote convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return jda.getEmoteById(from.split("<:")[1].split(":")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                Emote role = jda.getEmoteById(from);
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
