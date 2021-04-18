package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class RoleConverter extends Converter<Role> {

    public RoleConverter() {
        super(Role.class);
    }

    @Override
    public Role convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return guild.getRoleById(from.split("<@&")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                Role role = guild.getRoleById(from);
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
    public Role convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return jda.getRoleById(from.split("<@&")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                Role role = jda.getRoleById(from);
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
