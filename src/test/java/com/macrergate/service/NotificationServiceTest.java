package com.macrergate.service;

import java.time.LocalDate;
import java.time.LocalTime;

import com.macrergate.bot.VolleyBot;
import com.macrergate.model.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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

    @BeforeEach
    void setUp() {
        settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);
        settings.setCurrentGameTimeAsLocalTime(LocalTime.of(18, 0));
        settings.setCurrentGameDateAsLocalDate(LocalDate.now());
    }
    
    @Test
    void testSendOpenBookingNotification() {
        // Arrange
        when(settingsService.getSettings()).thenReturn(settings);
        
        // Act
        notificationService.sendOpenBookingNotification();
        
        // Assert
        verify(bot, times(1)).sendLoudMessageToMainGroup(any(String.class));
        verify(bookingService, times(1)).clearAllBookings();
        verify(settingsService, times(1)).updateCurrentGame(any(), any());
        verify(settingsService, times(1)).openBooking();
    }
    
    @Test
    void testSendCloseBookingNotification() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);

        // Act
        notificationService.sendCloseBookingNotification();
        
        // Assert
        verify(settingsService, times(1)).closeBooking();
        verify(bot, times(1)).sendMessageToAdmin(any(String.class), eq(true));
    }
    
    @Test
    void testSendCloseBookingNotificationWithBookingClosed() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(false);

        // Act
        notificationService.sendCloseBookingNotification();
        
        // Assert
        verifyNoMoreInteractions(bot, settingsService);
    }
}
