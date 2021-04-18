package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class UserConverter extends Converter<User> {

    public UserConverter() {
        super(User.class);
    }

    @Override
    public User convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            return jda.getUserById(from.split("<@!")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                User role = jda.getUserById(from);
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
    public User convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            return jda.getUserById(from.split("<@!")[1].split(">")[0]);
        } catch (Exception ignored) {
            try {
                User role = jda.getUserById(from);
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
