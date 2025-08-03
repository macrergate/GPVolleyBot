package com.macrergate.command;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.macrergate.model.Booking;
import com.macrergate.model.Settings;
import com.macrergate.service.BookingService;
import com.macrergate.service.BookingService.BookingResult;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookCommandHandlerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private BookCommandHandler bookCommandHandler;

    private Settings settings;
    private List<Booking> bookings;
    private Update update;
    private Message message;

    @BeforeEach
    void setUp() {
        // Настройка тестовых данных
        settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);

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
        User user = new User();
        Chat chat = new Chat();
        
        user.setId(123456789L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserName("testuser");
        
        chat.setId(987654321L);
        
        message.setFrom(user);
        message.setChat(chat);
        message.setText("/book 18:30");
        
        update.setMessage(message);
    }

    @Test
    void testExecute_Success() {
        // Arrange
        when(bookingService.bookGame(anyString(), anyString(), any(LocalTime.class)))
                .thenReturn(BookingResult.SUCCESS);
        when(bookingService.getAllBookings()).thenReturn(bookings);
        when(settingsService.getSettings()).thenReturn(settings);

        // Act
        String response = bookCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("✅ Вы успешно записаны на игру!");
        assertThat(response).contains("User 1");
        assertThat(response).contains("User 2");
    }
    
    @Test
    void testExecute_BookingClosed() {
        // Arrange
        when(bookingService.bookGame(anyString(), anyString(), any(LocalTime.class)))
                .thenReturn(BookingResult.BOOKING_CLOSED);

        // Act
        String response = bookCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("❌ Не удалось записаться: запись на игру закрыта.");
    }


    @Test
    void testExecute_PlayerLimitReached() {
        // Arrange
        when(bookingService.bookGame(anyString(), anyString(), any(LocalTime.class)))
                .thenReturn(BookingResult.PLAYER_LIMIT_REACHED);
        when(settingsService.getSettings()).thenReturn(settings);

        // Act
        String response = bookCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("❌ Не удалось записаться: достигнут лимит игроков");
        assertThat(response).contains(String.valueOf(Settings.DEFAULT_PLAYER_LIMIT));
    }

    @Test
    void testExecute_AlreadyBooked() {
        // Arrange
        when(bookingService.bookGame(anyString(), anyString(), any(LocalTime.class)))
                .thenReturn(BookingResult.ALREADY_BOOKED);

        // Act
        String response = bookCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("❌ Не удалось записаться: вы уже записаны на эту игру");
    }

    @Test
    void testExecute_InvalidTime() {
        // Arrange
        message.setText("/book invalid_time");

        // Act
        String response = bookCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("Неверный формат времени");
    }
    
    @Test
    void testGetCommandName() {
        // Act
        String commandName = bookCommandHandler.getCommandName();
        
        // Assert
        assertThat(commandName).isEqualTo("book");
    }
}
