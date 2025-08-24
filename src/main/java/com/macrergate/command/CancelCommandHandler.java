package com.macrergate.command;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /cancel для отмены записи на игру
 */
@Component
public class CancelCommandHandler extends AbstractCommand {
    
    public CancelCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }

    @Nonnull
    @Override
    public String execute(Update update) {
        String userId = getUserId(update);
        
        if (!settingsService.isBookingOpen()) {
            return "❌ Не удалось отменить запись: запись на игру закрыта.";
        }
        
        boolean success = bookingService.cancelBooking(userId);
        
        StringBuilder response = new StringBuilder();
        
        if (success) {
            response.append("✅ Ваша запись отменена.nn");
            response.append(generateBookingsList());
        } else {
            response.append("❌ Не удалось отменить запись: вы не были записаны на игру.");
        }
        
        return response.toString();
    }
    
    @Override
    public String getCommandName() {
        return "cancel";
    }
}
