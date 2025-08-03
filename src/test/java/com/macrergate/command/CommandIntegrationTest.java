package com.macrergate.command;

import com.macrergate.bot.VolleyBot;
import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест для проверки взаимодействия между командами
 */
@SpringBootTest
@ActiveProfiles("test")
public class CommandIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SettingsService settingsService;
    
    @MockBean
    private TelegramBotsApi telegramBotsApi;
    
    @MockBean
    private VolleyBot volleyBot;

    private BookCommandHandler bookCommandHandler;
    private OpenCommandHandler openCommandHandler;

    private Update update;
    private Message message;
    private User user;
    private Chat chat;

    @BeforeEach
    void setUp() {
        // Создаем обработчики команд с реальными сервисами
        bookCommandHandler = new BookCommandHandler(bookingService, settingsService);
        openCommandHandler = new OpenCommandHandler(bookingService, settingsService);

        // Настройка объектов Telegram API
        update = new Update();
        message = new Message();
        user = new User();
        chat = new Chat();

        user.setId(123456789L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserName("testuser");

        chat.setId(987654321L);

        message.setFrom(user);
        message.setChat(chat);
        message.setText("/book");

        update.setMessage(message);
    }

    /**
     * Тест проверяет сценарий:
     * 1. Попытка записаться командой /book, когда запись закрыта (должна не пройти)
     * 2. Открытие записи командой /open
     * 3. Повторная попытка записаться командой /book (должна пройти успешно)
     */
    @Test
    void testBookAfterOpenCommand() {
        // 1. Попытка записаться, когда запись закрыта
        String bookResponse1 = bookCommandHandler.execute(update);
        
        // Проверяем, что запись не прошла
        assertThat(bookResponse1).contains("❌ Не удалось записаться: запись на игру закрыта");
        assertThat(bookingService.getAllBookings()).isEmpty();

        // 2. Открываем запись
        message.setText("/open");
        String openResponse = openCommandHandler.execute(update);
        
        // Проверяем, что запись открыта
        assertThat(openResponse).contains("✅ Запись на игру открыта");
        assertThat(settingsService.isBookingOpen()).isTrue();

        // 3. Повторная попытка записаться
        message.setText("/book");
        String bookResponse2 = bookCommandHandler.execute(update);
        
        // Проверяем, что запись прошла успешно
        assertThat(bookResponse2).contains("✅ Вы успешно записаны на игру!");
        assertThat(bookingService.getAllBookings()).hasSize(1);
        assertThat(bookingService.getAllBookings().get(0).getUserId()).isEqualTo("123456789");
        assertThat(bookingService.getAllBookings().get(0).getDisplayName()).isEqualTo("testuser");
    }
}
