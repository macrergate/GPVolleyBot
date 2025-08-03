package com.macrergate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.macrergate.bot.VolleyBot;
import com.macrergate.command.CommandRegistry;

/**
 * Конфигурация бота для Telegram
 */
@Configuration
public class BotConfiguration {
    
    @Value("${bot.token}")
    private String botToken;
    
    @Value("${bot.username}")
    private String botUsername;
    
    @Value("${bot.chat-id}")
    private String chatId;
    
    @Bean
    public VolleyBot volleyBot(CommandRegistry commandRegistry) {
        return new VolleyBot(botToken, botUsername, chatId, commandRegistry);
    }
    
    @Bean
    public TelegramBotsApi telegramBotsApi(VolleyBot volleyBot) throws TelegramApiException {
        // Регистрируем бота
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(volleyBot);
        
        return api;
    }
}
