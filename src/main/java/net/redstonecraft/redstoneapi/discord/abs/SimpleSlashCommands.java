package net.redstonecraft.redstoneapi.discord.abs;

import net.redstonecraft.redstoneapi.discord.obj.SlashCommandContext;

/**
 * @author Redstonecrafter0
 * @since 1.4
 */
public interface SimpleSlashCommands {

    public default void handleError(SlashCommandContext ctx, Throwable throwable) {
        ctx.getSlashCommandEvent().reply("Internal error").queue();
    }

}
