package com.macrergate.command;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.macrergate.model.Booking;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OpenCommandHandlerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private OpenCommandHandler openCommandHandler;

    private Settings settings;
    private List<Booking> bookings;
    private Update update;
    private Update updateWithTime;

    @BeforeEach
    void setUp() {
        // Настройка тестовых данных
        settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);
        settings.setCurrentGameTimeAsLocalTime(LocalTime.of(18, 0)); // Время по умолчанию

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

        // Настройка объектов Telegram API для команды без параметров
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
        message.setText("/open");
        
        update.setMessage(message);

        // Настройка объектов Telegram API для команды с параметром времени
        updateWithTime = new Update();
        Message messageWithTime = new Message();
        messageWithTime.setFrom(user);
        messageWithTime.setChat(chat);
        messageWithTime.setText("/open 19:00");

        updateWithTime.setMessage(messageWithTime);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(false);
        when(bookingService.getAllBookings()).thenReturn(bookings);
        when(settingsService.getSettings()).thenReturn(settings);

        // Act
        String response = openCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("✅ Запись на игру открыта");
        assertThat(response).contains("User 1");
        assertThat(response).contains("User 2");
        verify(settingsService).openBooking();
        verify(settingsService).updateCurrentGame(eq(LocalTime.of(18, 0)), any(LocalDate.class));
    }

    @Test
    void testExecute_WithTimeParameter() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(false);
        when(bookingService.getAllBookings()).thenReturn(bookings);
        when(settingsService.getSettings()).thenReturn(settings);

        // Act
        String response = openCommandHandler.execute(updateWithTime);

        // Assert
        assertThat(response).contains("✅ Запись на игру открыта");
        verify(settingsService).openBooking();
        verify(settingsService).updateCurrentGame(eq(LocalTime.of(19, 0)), any(LocalDate.class));
    }
    
    @Test
    void testExecute_AlreadyOpen() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);

        // Act
        String response = openCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("❌ Запись на игру уже открыта");
        verify(settingsService, never()).updateCurrentGame(any(), any());
    }

    @Test
    void testExecute_AlreadyOpenWithTimeParameter() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        when(bookingService.getAllBookings()).thenReturn(bookings);
        when(settingsService.getSettings()).thenReturn(settings);

        // Act
        String response = openCommandHandler.execute(updateWithTime);

        // Assert
        assertThat(response).contains("✅ Время начала игры обновлено");
        verify(settingsService).updateCurrentGame(eq(LocalTime.of(19, 0)), any(LocalDate.class));
    }

    @Test
    void testExecute_InvalidTimeFormat() {
        // Arrange
        Update updateWithInvalidTime = new Update();
        Message messageWithInvalidTime = new Message();
        messageWithInvalidTime.setText("/open 19-00");
        updateWithInvalidTime.setMessage(messageWithInvalidTime);

        // Act
        String response = openCommandHandler.execute(updateWithInvalidTime);

        // Assert
        assertThat(response).contains("❌ Неверный формат времени");
        verify(settingsService, never()).updateCurrentGame(any(), any());
        verify(settingsService, never()).openBooking();
    }
    
    @Test
    void testGetCommandName() {
        // Act
        String commandName = openCommandHandler.getCommandName();
        
        // Assert
        assertThat(commandName).isEqualTo("open");
    }
}
