package com.macrergate.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;

/**
 * Обработчик команды /open для ручного открытия записи на игру
 */
@Component
public class OpenCommandHandler extends AbstractCommand {
    
    public OpenCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }
    
    @Override
    public String execute(Update update) {
        // Проверяем, открыта ли уже запись
        if (settingsService.isBookingOpen()) {
            return "❌ Запись на игру уже открыта.";
        }
        
        // Открываем запись
        settingsService.openBooking();
        
        return "✅ Запись на игру открыта.\n\n" + generateBookingsList();
    }
    
    @Override
    public String getCommandName() {
        return "open";
    }
}
