package com.macrergate.command;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import com.macrergate.service.BookingService;
import com.macrergate.service.BookingService.BookingResult;
import com.macrergate.service.SettingsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /book для записи на игру
 */
@Component
public class BookCommandHandler extends AbstractCommand {
    
    public BookCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }
    
    @Override
    public String execute(Update update) {
        String userId = getUserId(update);
        String displayName = getDisplayName(update);
        String[] args = getCommandArgs(update);
        
        LocalTime arrivalTime = null;
        if (args.length > 0) {
            try {
                arrivalTime = LocalTime.parse(args[0]);
            } catch (DateTimeParseException e) {
                return "Неверный формат времени. Используйте формат ЧЧ:ММ";
            }
        }
        
        BookingResult result = bookingService.bookGame(userId, displayName, arrivalTime);
        
        StringBuilder response = new StringBuilder();
        
        switch (result) {
            case SUCCESS:
                response.append("✅ Вы успешно записаны на игру!\n\n");
                response.append(generateBookingsList());
                break;
            case TIME_UPDATED:
                response.append("✅ Время вашего прихода обновлено!\n\n");
                response.append(generateBookingsList());
                break;
            case PLAYER_LIMIT_REACHED:
                response.append("❌ Не удалось записаться: достигнут лимит игроков (");
                response.append(settingsService.getSettings().getPlayerLimit());
                response.append(").");
                break;
            case ALREADY_BOOKED:
                response.append("❌ Не удалось записаться: вы уже записаны на эту игру.");
                response.append("\nЧтобы изменить время прихода, используйте команду /book ЧЧ:ММ");
                break;
            case BOOKING_CLOSED:
                response.append("❌ Не удалось записаться: запись на игру закрыта.");
                break;
            default:
                response.append("❌ Не удалось записаться: неизвестная ошибка.");
                break;
        }
        
        return response.toString();
    }
    
    @Override
    public String getCommandName() {
        return "book";
    }
}
