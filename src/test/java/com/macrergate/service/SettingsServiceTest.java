package com.macrergate.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import com.macrergate.model.Settings;
import com.macrergate.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SettingsServiceTest {

    @Mock
    private SettingsRepository settingsRepository;
    
    @InjectMocks
    private SettingsService settingsService;
    
    private Settings settings;
    
    @BeforeEach
    void setUp() {
        settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);
        settings.setCurrentGameTimeAsLocalTime(LocalTime.of(18, 0));
        settings.setCurrentGameDateAsLocalDate(LocalDate.now());
        settings.setBookingOpen(false);
    }
    
    @Test
    void testGetSettings() {
        // Arrange
        when(settingsRepository.findSettings()).thenReturn(Optional.of(settings));
        
        // Act
        Settings result = settingsService.getSettings();
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPlayerLimit()).isEqualTo(Settings.DEFAULT_PLAYER_LIMIT);
        assertThat(result.isBookingOpen()).isFalse();
    }
    
    @Test
    void testGetSettingsWhenNotExists() {
        // Arrange
        when(settingsRepository.findSettings()).thenReturn(Optional.empty());
        when(settingsRepository.save(any(Settings.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        Settings result = settingsService.getSettings();
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPlayerLimit()).isEqualTo(Settings.DEFAULT_PLAYER_LIMIT); // Default value
        assertThat(result.isBookingOpen()).isFalse(); // Default value
        verify(settingsRepository, times(1)).save(any(Settings.class));
    }
    
    @Test
    void testUpdatePlayerLimit() {
        // Arrange
        when(settingsRepository.findSettings()).thenReturn(Optional.of(settings));
        when(settingsRepository.save(any(Settings.class))).thenReturn(settings);
        
        // Act
        settingsService.updatePlayerLimit(25);
        
        // Assert
        verify(settingsRepository, times(1)).save(any(Settings.class));
        assertThat(settings.getPlayerLimit()).isEqualTo(25);
    }

    @Test
    void testUpdateCurrentGame() {
        // Arrange
        when(settingsRepository.findSettings()).thenReturn(Optional.of(settings));
        when(settingsRepository.save(any(Settings.class))).thenReturn(settings);
        
        // Act
        settingsService.updateCurrentGame(LocalTime.of(19, 30), LocalDate.now().plusDays(1));
        
        // Assert
        verify(settingsRepository, times(1)).save(any(Settings.class));
        assertThat(settings.getCurrentGameTimeAsLocalTime()).isEqualTo(LocalTime.of(19, 30));
        assertThat(settings.getCurrentGameDateAsLocalDate()).isEqualTo(LocalDate.now().plusDays(1));
    }
    
    @Test
    void testIsBookingOpenWhenTrueAndGameToday() {
        // Arrange
        settings.setBookingOpen(true);
        when(settingsRepository.findSettings()).thenReturn(Optional.of(settings));
        
        // Act
        boolean result = settingsService.isBookingOpen();
        
        // Assert
        assertThat(result).isTrue();
    }
    
    @Test
    void testIsBookingOpenWhenTrueButNoGameToday() {
        // Arrange
        settings.setBookingOpen(true);
        settings.setCurrentGameDateAsLocalDate(LocalDate.now().plusDays(1)); // Игра завтра
        when(settingsRepository.findSettings()).thenReturn(Optional.of(settings));
        
        // Act
        boolean result = settingsService.isBookingOpen();
        
        // Assert
        // После изменения логики isBookingOpen() больше не проверяет, есть ли игра сегодня
        assertThat(result).isTrue();
    }
    
    @Test
    void testIsBookingOpenWhenFalse() {
        // Arrange
        settings.setBookingOpen(false);
        when(settingsRepository.findSettings()).thenReturn(Optional.of(settings));
        
        // Act
        boolean result = settingsService.isBookingOpen();
        
        // Assert
        assertThat(result).isFalse();
    }
    
    @Test
    void testOpenBooking() {
        // Arrange
        when(settingsRepository.findSettings()).thenReturn(Optional.of(settings));
        when(settingsRepository.save(any(Settings.class))).thenReturn(settings);
        
        // Act
        settingsService.openBooking();
        
        // Assert
        verify(settingsRepository, times(1)).save(any(Settings.class));
        assertThat(settings.isBookingOpen()).isTrue();
    }
    
    @Test
    void testCloseBooking() {
        // Arrange
        settings.setBookingOpen(true);
        when(settingsRepository.findSettings()).thenReturn(Optional.of(settings));
        when(settingsRepository.save(any(Settings.class))).thenReturn(settings);
        
        // Act
        settingsService.closeBooking();
        
        // Assert
        verify(settingsRepository, times(1)).save(any(Settings.class));
        assertThat(settings.isBookingOpen()).isFalse();
    }
}
