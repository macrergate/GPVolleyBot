package com.macrergate.scheduler;

import java.time.LocalDate;
import java.time.LocalTime;

import com.macrergate.service.NotificationService;
import com.macrergate.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {
    private final NotificationService notificationService;
    private final SettingsService settingsService;

    // Запуск уведомления каждый вторник в 9:00
    @Scheduled(cron = "0 0 10 ? * TUE")
    public void sendTuesdayNotification() {
        settingsService.updateCurrentGame(LocalTime.of(18, 0), LocalDate.now());

        notificationService.sendOpenBookingNotification();
    }

    // Запуск уведомления каждый четверг в 10:00
    @Scheduled(cron = "0 10 10 ? * THU")
    public void sendThursdayNotification() {
        settingsService.updateCurrentGame(LocalTime.of(18, 0), LocalDate.now());

        notificationService.sendOpenBookingNotification();
    }

    // Запуск уведомления каждое воскресенье в 10:00
    @Scheduled(cron = "0 0 10 ? * SUN")
    public void sendSundayNotification() {
        settingsService.updateCurrentGame(LocalTime.of(17, 0), LocalDate.now());

        notificationService.sendOpenBookingNotification();
    }
    
    // Закрытие записи каждый день в 21:00
    @Scheduled(cron = "0 0 21 * * ?")
    public void closeBooking() {
        if (settingsService.isBookingOpen()) {
            notificationService.sendCloseBookingNotification();
        }
    }
}
