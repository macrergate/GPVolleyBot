package com.macrergate.command;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /open для ручного открытия записи на игру
 */
@Component
public class OpenCommandHandler extends AbstractCommand {

    private static final LocalTime DEFAULT_GAME_TIME = LocalTime.of(18, 0); // Время по умолчанию - 18:00
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public OpenCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }

    @Nonnull
    @Override
    public String execute(Update update) {
        String[] args = getCommandArgs(update);
        LocalTime gameTime = DEFAULT_GAME_TIME;
        boolean isAlreadyOpen = settingsService.isBookingOpen();

        // Если есть аргумент с временем, пробуем его распарсить
        if (args.length > 0) {
            try {
                gameTime = LocalTime.parse(args[0], TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                return "❌ Неверный формат времени. Используйте формат ЧЧ:ММ, например, 19:00";
            }
        } else if (isAlreadyOpen) {
            // Если нет аргумента и запись уже открыта, просто возвращаем сообщение без изменения времени
            return "❌ Запись на игру уже открыта.";
        }

        // Обновляем время начала игры
        settingsService.updateCurrentGame(gameTime, LocalDate.now());

        // Открываем запись, если она еще не открыта
        if (!isAlreadyOpen) {
            settingsService.openBooking();
            return "✅ Запись на игру открыта.\n\n" + generateBookingsList();
        } else {
            // Если запись уже открыта и указано новое время
            return "✅ Время начала игры обновлено.\n\n" + generateBookingsList();
        }
    }
    
    @Override
    public String getCommandName() {
        return "open";
    }
}
