package net.redstonecraft.redstoneapi.discord.abs;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SimpleCommand {

    String name() default "";
    String usage();
    String info();
    Permission permission() default Permission.UNKNOWN;

}
