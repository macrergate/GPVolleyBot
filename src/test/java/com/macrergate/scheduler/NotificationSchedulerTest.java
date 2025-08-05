package com.macrergate.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;

import com.macrergate.model.Settings;
import com.macrergate.service.NotificationService;
import com.macrergate.service.SettingsService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotificationSchedulerTest {

    @Mock
    private NotificationService notificationService;
    
    @Mock
    private SettingsService settingsService;
    
    @InjectMocks
    private NotificationScheduler notificationScheduler;

    @BeforeEach
    void setUp() {
        Settings settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);
        settings.setCurrentGameTimeAsLocalTime(LocalTime.of(18, 0));
        settings.setCurrentGameDateAsLocalDate(LocalDate.now());
    }
    
    @Test
    void testSendTuesdayNotification() {
        // Act
        notificationScheduler.sendTuesdayNotification();
        
        // Assert
        verify(settingsService, times(1)).updateCurrentGame(eq(LocalTime.of(18, 0)), any(LocalDate.class));
        verify(notificationService, times(1)).sendOpenBookingNotification();
    }
    
    @Test
    void testSendThursdayNotification() {
        // Act
        notificationScheduler.sendThursdayNotification();
        
        // Assert
        verify(settingsService, times(1)).updateCurrentGame(eq(LocalTime.of(18, 0)), any(LocalDate.class));
        verify(notificationService, times(1)).sendOpenBookingNotification();
    }
    
    @Test
    void testSendSundayNotification() {
        // Act
        notificationScheduler.sendSundayNotification();
        
        // Assert
        verify(settingsService, times(1)).updateCurrentGame(eq(LocalTime.of(17, 0)), any(LocalDate.class));
        verify(notificationService, times(1)).sendOpenBookingNotification();
    }
    
    @Test
    void testCloseBooking_WhenBookingOpen() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        
        // Act
        notificationScheduler.closeBooking();
        
        // Assert
        verify(notificationService, times(1)).sendCloseBookingNotification();
    }

    @Test
    void testCloseBooking_WhenBookingNotOpen() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(false);
        
        // Act
        notificationScheduler.closeBooking();
        
        // Assert
        verify(notificationService, times(0)).sendCloseBookingNotification();
    }
}
