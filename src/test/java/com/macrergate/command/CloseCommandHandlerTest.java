package com.macrergate.command;

import com.macrergate.model.Settings;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CloseCommandHandlerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private CloseCommandHandler closeCommandHandler;

    private Update update;

    @BeforeEach
    void setUp() {
        // Настройка тестовых данных
        Settings settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);

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
        message.setText("/close");
        
        update.setMessage(message);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);

        // Act
        String response = closeCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("✅ Запись на игру закрыта");
        verify(settingsService).closeBooking();
    }
    
    @Test
    void testExecute_AlreadyClosed() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(false);

        // Act
        String response = closeCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("❌ Запись на игру уже закрыта");
    }
    
    @Test
    void testGetCommandName() {
        // Act
        String commandName = closeCommandHandler.getCommandName();
        
        // Assert
        assertThat(commandName).isEqualTo("close");
    }
}
