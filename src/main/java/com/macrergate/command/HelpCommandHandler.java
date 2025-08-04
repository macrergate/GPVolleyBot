package com.macrergate.command;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /help для вывода справки о боте и доступных командах
 */
@Component
public class HelpCommandHandler extends AbstractCommand {

    public HelpCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }

    @Override
    public String execute(Update update) {

        String response = "🏐 *Волейбольный бот* 🏐\n\n" +
                "Этот бот помогает организовывать запись на волейбольные игры.\n\n" +
                "*Доступные команды:*\n\n" +
                "📝 */book* [время] - записаться на игру. Можно указать время прихода в формате ЧЧ:ММ\n" +
                "❌ */cancel* - отменить свою запись на игру\n" +
                "📋 */list* - показать список записавшихся на игру\n" +
                "🔓 */open* - открыть запись на игру (для администраторов)\n" +
                "🔒 */close* - закрыть запись на игру (для администраторов)\n" +
                "🔢 */limit* [число] - изменить лимит игроков (для администраторов)\n" +
                "❓ */help* - показать эту справку\n\n" +
                "Бот автоматически отправляет сообщения о своем статусе при запуске и завершении работы.";

        return response;
    }

    @Override
    public String getCommandName() {
        return "help";
    }
}
