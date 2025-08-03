package com.macrergate.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.macrergate.bot.VolleyBot;
import com.macrergate.model.Booking;
import com.macrergate.model.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private VolleyBot bot;
    
    @Mock
    private SettingsService settingsService;
    
    @Mock
    private BookingService bookingService;
    
    @InjectMocks
    private NotificationService notificationService;
    
    private Settings settings;
    private List<Booking> bookings;
    private final String chatId = "123456789";
    
    @BeforeEach
    void setUp() {
        settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(21);
        settings.setCurrentGameDay("Вторник");
        settings.setCurrentGameTimeAsLocalTime(LocalTime.of(18, 0));
        settings.setCurrentGameDateAsLocalDate(LocalDate.now());
        
        // Создаем тестовые записи
        bookings = new ArrayList<>();
        Booking booking1 = new Booking();
        booking1.setUserId("user1");
        booking1.setDisplayName("User 1");
        booking1.setBookingTimeAsLocalDateTime(LocalDate.now().atTime(10, 0));
        booking1.setArrivalTimeAsLocalTime(LocalTime.of(18, 30));
        
        Booking booking2 = new Booking();
        booking2.setUserId("user2");
        booking2.setDisplayName("User 2");
        booking2.setBookingTimeAsLocalDateTime(LocalDate.now().atTime(11, 0));
        
        bookings.add(booking1);
        bookings.add(booking2);
    }
    
    @Test
    void testSendOpenBookingNotification() throws TelegramApiException {
        // Arrange
        when(settingsService.getSettings()).thenReturn(settings);
        
        // Act
        notificationService.sendOpenBookingNotification(chatId);
        
        // Assert
        verify(bot, times(1)).execute(any(SendMessage.class));
        verify(bookingService, times(1)).clearAllBookings();
        verify(settingsService, times(1)).updateCurrentGame(any(), any(), any());
        verify(settingsService, times(1)).openBooking();
    }
    
    @Test
    void testSendCloseBookingNotification() throws TelegramApiException {
        // Arrange
        when(settingsService.getSettings()).thenReturn(settings);
        when(bookingService.getAllBookings()).thenReturn(bookings);
        
        // Act
        notificationService.sendCloseBookingNotification(chatId);
        
        // Assert
        verify(bot, times(1)).execute(any(SendMessage.class));
        verify(settingsService, times(1)).closeBooking();
    }
    
    @Test
    void testSendCloseBookingNotificationWithNoBookings() throws TelegramApiException {
        // Arrange
        when(settingsService.getSettings()).thenReturn(settings);
        when(bookingService.getAllBookings()).thenReturn(new ArrayList<>());
        
        // Act
        notificationService.sendCloseBookingNotification(chatId);
        
        // Assert
        verify(bot, times(1)).execute(any(SendMessage.class));
        verify(settingsService, times(1)).closeBooking();
    }
}
