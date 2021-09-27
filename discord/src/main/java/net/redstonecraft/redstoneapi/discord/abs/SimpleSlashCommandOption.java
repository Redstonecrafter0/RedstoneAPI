package net.redstonecraft.redstoneapi.discord.abs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SimpleSlashCommandOption {

    String name();
    String info();
    boolean optional() default false;
    String[] stringChoices() default {};
    int[] intChoices() default {};

}
