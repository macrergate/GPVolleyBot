package com.macrergate.command;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /list для просмотра списка записавшихся на игру
 */
@Component
public class ListCommandHandler extends AbstractCommand {
    
    public ListCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }
    
    @Override
    public String execute(Update update) {
        return execute();
    }

    public String execute() {
        return generateBookingsList();
    }

    @Override
    public String getCommandName() {
        return "list";
    }
}
