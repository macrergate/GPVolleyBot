package com.macrergate.command;

import java.util.Arrays;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;

import lombok.RequiredArgsConstructor;

/**
 * Абстрактный класс для обработчиков команд бота
 */
@RequiredArgsConstructor
public abstract class AbstractCommand implements Command {
    
    protected final BookingService bookingService;
    protected final SettingsService settingsService;
    
    /**
     * Извлекает аргументы команды из сообщения
     * 
     * @param update Объект обновления от Telegram
     * @return Массив аргументов команды
     */
    protected String[] getCommandArgs(Update update) {
        String messageText = update.getMessage().getText();
        String[] parts = messageText.split(" ");
        return parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];
    }
    
    /**
     * Получает идентификатор пользователя из обновления
     * 
     * @param update Объект обновления от Telegram
     * @return Идентификатор пользователя
     */
    protected String getUserId(Update update) {
        return update.getMessage().getFrom().getId().toString();
    }
    
    /**
     * Получает отображаемое имя пользователя
     * 
     * @param update Объект обновления от Telegram
     * @return Отображаемое имя пользователя
     */
    protected String getDisplayName(Update update) {
        User user = update.getMessage().getFrom();
        if (user.getUserName() != null) {
            return user.getUserName();
        } else if (user.getLastName() != null) {
            return user.getFirstName() + " " + user.getLastName();
        } else {
            return user.getFirstName();
        }
    }
    
    /**
     * Генерирует список записавшихся на игру
     * 
     * @return Текстовое представление списка записавшихся
     */
    protected String generateBookingsList() {
        var bookings = bookingService.getAllBookings();
        if (bookings.isEmpty()) {
            return "На сегодня нет записей.";
        }
        
        StringBuilder sb = new StringBuilder("Список записавшихся на игру:\n");
        for (int i = 0; i < bookings.size(); i++) {
            var booking = bookings.get(i);
            sb.append(i + 1).append(". ").append(booking.getDisplayName());
            if (booking.getArrivalTimeAsLocalTime() != null) {
                sb.append(" (").append(booking.getArrivalTimeAsLocalTime()).append(")");
            }
            sb.append("\n");
        }
        
        var settings = settingsService.getSettings();
        sb.append("\nВсего: ").append(bookings.size()).append("/").append(settings.getPlayerLimit());
        
        return sb.toString();
    }
}
