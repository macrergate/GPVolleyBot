package com.macrergate.service;

import java.time.LocalDate;
import java.time.LocalTime;

import com.macrergate.model.Settings;
import com.macrergate.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {
    private final SettingsRepository settingsRepository;
    
    public Settings getSettings() {
        return settingsRepository.findSettings()
                .orElseGet(() -> {
                    Settings settings = new Settings();
                    settings.setId(1L);
                    settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);
                    return settingsRepository.save(settings);
                });
    }
    
    public void updatePlayerLimit(int limit) {
        Settings settings = getSettings();
        settings.setPlayerLimit(limit);
        settingsRepository.save(settings);
    }

    public void updateCurrentGame(LocalTime time, LocalDate date) {
        Settings settings = getSettings();
        settings.setCurrentGameTimeAsLocalTime(time);
        settings.setCurrentGameDateAsLocalDate(date);
        settingsRepository.save(settings);
    }

    public boolean isBookingOpen() {
        Settings settings = getSettings();
        return settings.isBookingOpen();
    }
    
    public void openBooking() {
        Settings settings = getSettings();
        settings.setBookingOpen(true);
        settingsRepository.save(settings);
    }
    
    public void closeBooking() {
        Settings settings = getSettings();
        settings.setBookingOpen(false);
        settingsRepository.save(settings);
    }
}
