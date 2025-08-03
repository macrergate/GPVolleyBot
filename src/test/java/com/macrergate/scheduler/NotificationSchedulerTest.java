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
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    
    private Settings settings;
    private final String chatId = "123456789";
    
    @BeforeEach
    void setUp() {
        settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);
        settings.setCurrentGameDay("Вторник");
        settings.setCurrentGameTimeAsLocalTime(LocalTime.of(18, 0));
        settings.setCurrentGameDateAsLocalDate(LocalDate.now());
        
        // Устанавливаем значение chatId через рефлексию
        ReflectionTestUtils.setField(notificationScheduler, "chatId", chatId);
    }
    
    @Test
    void testSendTuesdayNotification() {
        // Arrange
        when(settingsService.getSettings()).thenReturn(settings);
        
        // Act
        notificationScheduler.sendTuesdayNotification();
        
        // Assert
        verify(settingsService, times(1)).updateCurrentGame(eq("Вторник"), eq(LocalTime.of(18, 0)), any(LocalDate.class));
        verify(notificationService, times(1)).sendOpenBookingNotification(chatId);
    }
    
    @Test
    void testSendThursdayNotification() {
        // Act
        notificationScheduler.sendThursdayNotification();
        
        // Assert
        verify(settingsService, times(1)).updateCurrentGame(eq("Четверг"), eq(LocalTime.of(18, 0)), any(LocalDate.class));
        verify(notificationService, times(1)).sendOpenBookingNotification(chatId);
    }
    
    @Test
    void testSendSundayNotification() {
        // Act
        notificationScheduler.sendSundayNotification();
        
        // Assert
        verify(settingsService, times(1)).updateCurrentGame(eq("Воскресенье"), eq(LocalTime.of(17, 0)), any(LocalDate.class));
        verify(notificationService, times(1)).sendOpenBookingNotification(chatId);
    }
    
    @Test
    void testCloseBooking_WhenGameTodayAndBookingOpen() {
        // Arrange
        when(settingsService.isGameToday()).thenReturn(true);
        when(settingsService.isBookingOpen()).thenReturn(true);
        
        // Act
        notificationScheduler.closeBooking();
        
        // Assert
        verify(notificationService, times(1)).sendCloseBookingNotification(chatId);
    }
    
    @Test
    void testCloseBooking_WhenNoGameToday() {
        // Arrange
        when(settingsService.isGameToday()).thenReturn(false);
        
        // Act
        notificationScheduler.closeBooking();
        
        // Assert
        verify(notificationService, times(0)).sendCloseBookingNotification(anyString());
    }
    
    @Test
    void testCloseBooking_WhenBookingNotOpen() {
        // Arrange
        when(settingsService.isGameToday()).thenReturn(true);
        when(settingsService.isBookingOpen()).thenReturn(false);
        
        // Act
        notificationScheduler.closeBooking();
        
        // Assert
        verify(notificationService, times(0)).sendCloseBookingNotification(anyString());
    }
}
