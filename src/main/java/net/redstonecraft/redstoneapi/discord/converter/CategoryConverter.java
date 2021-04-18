package net.redstonecraft.redstoneapi.discord.converter;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.redstonecraft.redstoneapi.discord.abs.Converter;

public class CategoryConverter extends Converter<Category> {

    public CategoryConverter() {
        super(Category.class);
    }

    @Override
    public Category convertServer(String from, JDA jda, Message message, TextChannel channel, Member member, Guild guild) throws ConvertException {
        try {
            Category category = guild.getCategoryById(from);
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
    public Category convertPrivate(String from, JDA jda, Message message, PrivateChannel channel, User user) throws ConvertException {
        try {
            Category category = jda.getCategoryById(from);
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
