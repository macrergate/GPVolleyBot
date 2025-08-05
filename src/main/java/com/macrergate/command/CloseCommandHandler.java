package com.macrergate.command;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /close для ручного закрытия записи на игру
 */
@Component
public class CloseCommandHandler extends AbstractCommand {
    
    public CloseCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }
    
    @Override
    public String execute(Update update) {
        // Проверяем, закрыта ли уже запись
        if (!settingsService.isBookingOpen()) {
            return "❌ Запись на игру уже закрыта.";
        }
        
        // Закрываем запись
        settingsService.closeBooking();

        return "✅ Запись на игру закрыта.";
    }
    
    @Override
    public String getCommandName() {
        return "close";
    }
}
