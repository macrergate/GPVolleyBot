package com.macrergate.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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

import com.macrergate.model.Booking;
import com.macrergate.model.Settings;
import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;

@ExtendWith(MockitoExtension.class)
public class CloseCommandHandlerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private CloseCommandHandler closeCommandHandler;

    private Settings settings;
    private List<Booking> bookings;
    private Update update;
    private Message message;
    private User user;
    private Chat chat;

    @BeforeEach
    void setUp() {
        // Настройка тестовых данных
        settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(21);

        bookings = new ArrayList<>();
        
        // Добавляем несколько записей
        Booking booking1 = new Booking();
        booking1.setUserId("user1");
        booking1.setDisplayName("User 1");
        bookings.add(booking1);
        
        Booking booking2 = new Booking();
        booking2.setUserId("user2");
        booking2.setDisplayName("User 2");
        bookings.add(booking2);
        
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
        message.setText("/close");
        
        update.setMessage(message);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        when(bookingService.getAllBookings()).thenReturn(bookings);
        when(settingsService.getSettings()).thenReturn(settings);

        // Act
        String response = closeCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("✅ Запись на игру закрыта");
        assertThat(response).contains("User 1");
        assertThat(response).contains("User 2");
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
