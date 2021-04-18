package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class VoiceChannelConverter extends Converter<VoiceChannel> {

    public VoiceChannelConverter() {
        super(VoiceChannel.class);
    }

    @Override
    public VoiceChannel convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            VoiceChannel category = guild.getVoiceChannelById(from);
            if (category != null) {
                return category;
            } else {
                throw new ConvertException();
            }
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }

    @Override
    public VoiceChannel convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            VoiceChannel category = jda.getVoiceChannelById(from);
            if (category != null) {
                return category;
            } else {
                throw new ConvertException();
            }
        } catch (Exception ignored) {
            throw new ConvertException();
        }
    }
}
