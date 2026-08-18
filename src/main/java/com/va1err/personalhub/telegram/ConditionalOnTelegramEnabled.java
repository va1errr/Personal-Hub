package com.va1err.personalhub.telegram;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ConditionalOnProperty(
    prefix = "telegram.bot",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public @interface ConditionalOnTelegramEnabled {
}
