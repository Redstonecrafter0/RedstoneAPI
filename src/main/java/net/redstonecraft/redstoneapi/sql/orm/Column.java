package net.redstonecraft.redstoneapi.sql.orm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    boolean primaryKey() default false;
    boolean notnull() default false;
    boolean unique() default false;
    boolean hideOnJson() default false;
}
