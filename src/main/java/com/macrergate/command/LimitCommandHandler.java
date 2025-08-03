package com.macrergate.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;

/**
 * Обработчик команды /limit для изменения лимита игроков
 */
@Component
public class LimitCommandHandler extends AbstractCommand {
    
    public LimitCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }
    
    @Override
    public String execute(Update update) {
        String[] args = getCommandArgs(update);
        
        if (!settingsService.isBookingOpen()) {
            return "❌ Не удалось изменить лимит: запись на игру закрыта.";
        }
        
        if (args.length == 0) {
            return "Укажите новый лимит игроков.";
        }
        
        try {
            int limit = Integer.parseInt(args[0]);
            if (limit <= 0) {
                return "❌ Лимит должен быть положительным числом.";
            }
            
            settingsService.updatePlayerLimit(limit);
            return "✅ Лимит игроков изменен на " + limit + ".\n\n" + generateBookingsList();
        } catch (NumberFormatException e) {
            return "❌ Неверный формат числа.";
        }
    }
    
    @Override
    public String getCommandName() {
        return "limit";
    }
}
