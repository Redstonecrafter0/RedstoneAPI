package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class MemberConverter extends Converter<Member> {

    public MemberConverter() {
        super(Member.class);
    }

    @Override
    public Member convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return guild.getMemberById(from.split("<@!")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                Member role = guild.getMemberById(from);
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
    public Member convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        throw new ConvertException();
    }
}
