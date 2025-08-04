package com.macrergate.command;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class HelpCommandHandlerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private HelpCommandHandler helpCommandHandler;

    private Update update;

    @BeforeEach
    void setUp() {
        // Настройка объектов Telegram API
        update = new Update();
        Message message = new Message();
        User user = new User();
        Chat chat = new Chat();

        user.setId(123456789L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserName("testuser");

        chat.setId(987654321L);

        message.setFrom(user);
        message.setChat(chat);
        message.setText("/help");

        update.setMessage(message);
    }

    @Test
    void testExecute() {
        // Act
        String response = helpCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("Волейбольный бот");
        assertThat(response).contains("Доступные команды");
        assertThat(response).contains("/book");
        assertThat(response).contains("/cancel");
        assertThat(response).contains("/list");
        assertThat(response).contains("/open");
        assertThat(response).contains("/close");
        assertThat(response).contains("/limit");
        assertThat(response).contains("/help");
    }

    @Test
    void testGetCommandName() {
        // Act
        String commandName = helpCommandHandler.getCommandName();

        // Assert
        assertThat(commandName).isEqualTo("help");
    }
}
