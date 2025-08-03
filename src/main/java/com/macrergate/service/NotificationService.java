package com.macrergate.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.macrergate.bot.VolleyBot;
import com.macrergate.model.Booking;
import com.macrergate.model.Settings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final VolleyBot bot;
    private final SettingsService settingsService;
    private final BookingService bookingService;
    
    public void sendOpenBookingNotification(String chatId) {
        Settings settings = settingsService.getSettings();
        LocalTime gameTime = settings.getCurrentGameTimeAsLocalTime();
        String gameDay = settings.getCurrentGameDay();
        
        if (gameTime == null || gameDay == null) {
            return;
        }
        
        // Очищаем все предыдущие записи
        bookingService.clearAllBookings();
        
        // Обновляем дату текущей игры
        settingsService.updateCurrentGame(
                gameDay,
                gameTime,
                LocalDate.now()
        );
        
        // Открываем запись
        settingsService.openBooking();
        
        String message = "🏐 Запись на игру сегодня (" + gameDay + ") в " + gameTime + " открыта!\n" +
                         "Для записи используйте команду /book\n" +
                         "Лимит игроков: " + settings.getPlayerLimit();
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            // Обработка ошибки
        }
    }
    
    public void sendCloseBookingNotification(String chatId) {
        Settings settings = settingsService.getSettings();
        LocalTime gameTime = settings.getCurrentGameTimeAsLocalTime();
        String gameDay = settings.getCurrentGameDay();
        
        if (gameTime == null || gameDay == null) {
            return;
        }
        
        // Закрываем запись
        settingsService.closeBooking();
        
        List<Booking> bookings = bookingService.getAllBookings();
        int totalPlayers = bookings.size();

        StringBuilder message = new StringBuilder("🏐 Запись на игру сегодня (" + gameDay + ") в " + gameTime + " " +
                "закрыта!\n" +
                "Всего записалось: " + totalPlayers + "/" + settings.getPlayerLimit() + " игроков.\n\n");
        
        if (!bookings.isEmpty()) {
            message.append("Список записавшихся:\n");
            int counter = 1;
            for (Booking booking : bookings) {
                String arrivalTime = booking.getArrivalTimeAsLocalTime() != null
                    ? " (" + booking.getArrivalTimeAsLocalTime() + ")"
                    : "";
                message.append(counter).append(". ").append(booking.getDisplayName()).append(arrivalTime).append("\n");
                counter++;
            }
        } else {
            message.append("Никто не записался на игру.");
        }
        
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message.toString());
        
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            // Обработка ошибки
        }
    }
}
