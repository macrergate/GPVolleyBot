package com.macrergate.service;

import java.time.LocalDate;
import java.time.LocalTime;

import com.macrergate.bot.VolleyBot;
import com.macrergate.config.BotProperties;
import com.macrergate.model.Settings;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final VolleyBot bot;
    private final SettingsService settingsService;
    private final BookingService bookingService;
    private final BotProperties botProperties;

    @PostConstruct
    void init() {
        if (botProperties.isResetOnStart()) {
            settingsService.closeBooking();
            sendOpenBookingNotification();
        }
    }

    public void sendOpenBookingNotification() {
        if (settingsService.isBookingOpen()) {
            return;
        }

        Settings settings = settingsService.getSettings();
        LocalTime gameTime = settings.getCurrentGameTimeAsLocalTime();

        // Очищаем все предыдущие записи
        bookingService.clearAllBookings();
        
        // Обновляем дату текущей игры
        settingsService.updateCurrentGame(
                gameTime,
                LocalDate.now()
        );
        
        // Открываем запись
        settingsService.openBooking();

        String message = "🏐 Запись на игру сегодня в " + gameTime + " открыта!\n" +
                         "Для записи используйте команду /book\n" +
                         "Лимит игроков: " + settings.getPlayerLimit();

        bot.sendLoudMessageToMainGroup(message);
    }

    public void sendCloseBookingNotification() {
        if (!settingsService.isBookingOpen()) {
            return;
        }
        
        // Закрываем запись
        settingsService.closeBooking();

        String message = "🏐 Запись на игру закрыта.";

        bot.sendMessageToAdmin(message, true);
    }
}
