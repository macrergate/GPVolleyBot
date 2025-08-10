package com.macrergate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Настройки бота для Telegram
 */
@Component
@ConfigurationProperties(prefix = "bot")
@Data
public class BotProperties {

    /**
     * Токен бота
     */
    private String token;

    /**
     * Имя пользователя бота
     */
    private String username;

    /**
     * ID чата группы
     */
    private long chatId;

    /**
     * ID чата администратора
     */
    private long adminChatId;

    private boolean resetOnStart = false;
}
