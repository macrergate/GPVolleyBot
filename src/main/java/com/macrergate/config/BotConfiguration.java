package com.macrergate.config;

import com.macrergate.bot.VolleyBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Конфигурация бота для Telegram
 */
@Configuration
public class BotConfiguration {
    @Bean
    public TelegramBotsApi telegramBotsApi(VolleyBot volleyBot) throws TelegramApiException {
        // Регистрируем бота
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(volleyBot);
        
        return api;
    }
}
