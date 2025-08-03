package com.macrergate.config;

import com.macrergate.bot.VolleyBot;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Тестовая конфигурация для бота
 * Заменяет TelegramBotsApi на мок во время тестирования
 */
@TestConfiguration
@Profile("test")
public class TestBotConfiguration {
    
    /**
     * Заменяем TelegramBotsApi на мок, чтобы избежать ошибок при регистрации бота
     */
    @Bean
    @Primary
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        // Создаем мок TelegramBotsApi
        TelegramBotsApi mockApi = Mockito.mock(TelegramBotsApi.class);
        // Настраиваем мок, чтобы метод registerBot не выбрасывал исключение
        Mockito.doNothing().when(mockApi).registerBot(Mockito.any(VolleyBot.class));
        return mockApi;
    }
}
