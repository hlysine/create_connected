package com.hlysine.create_connected.compat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Conditionally enable/disable a mixin based on the presence of other mods.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModMixin {
    
    String[] mods();

    boolean applyIfPresent() default true;
}
