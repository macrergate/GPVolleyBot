package com.macrergate.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.macrergate.model.Settings;
import com.macrergate.service.NotificationService;
import com.macrergate.service.SettingsService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final SettingsService settingsService;
    
    @Value("${bot.chat-id}")
    private String chatId;
    
    // Запуск уведомления каждый вторник в 8:00
    @Scheduled(cron = "0 0 8 ? * TUE")
    public void sendTuesdayNotification() {
        Settings settings = settingsService.getSettings();
        settings.setCurrentGameDay("Вторник");
        settings.setCurrentGameTimeAsLocalTime(LocalTime.of(18, 0));
        settingsService.updateCurrentGame("Вторник", LocalTime.of(18, 0), LocalDate.now());
        
        notificationService.sendOpenBookingNotification(chatId);
    }
    
    // Запуск уведомления каждый четверг в 8:00
    @Scheduled(cron = "0 0 8 ? * THU")
    public void sendThursdayNotification() {
        settingsService.updateCurrentGame("Четверг", LocalTime.of(18, 0), LocalDate.now());
        
        notificationService.sendOpenBookingNotification(chatId);
    }
    
    // Запуск уведомления каждое воскресенье в 8:00
    @Scheduled(cron = "0 0 8 ? * SUN")
    public void sendSundayNotification() {
        settingsService.updateCurrentGame("Воскресенье", LocalTime.of(17, 0), LocalDate.now());
        
        notificationService.sendOpenBookingNotification(chatId);
    }
    
    // Закрытие записи каждый день в 21:00
    @Scheduled(cron = "0 0 21 * * ?")
    public void closeBooking() {
        if (settingsService.isGameToday() && settingsService.isBookingOpen()) {
            notificationService.sendCloseBookingNotification(chatId);
        }
    }
}
