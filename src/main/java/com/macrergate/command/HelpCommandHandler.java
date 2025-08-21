package com.macrergate.command;

import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Обработчик команды /help для вывода справки о боте и доступных командах
 */
@Component
public class HelpCommandHandler extends AbstractCommand {

    private static final String HELP = """
            🏐 *Волейбольный бот* 🏐
            
            Этот бот помогает организовывать запись на волейбольные игры.
            
            *Доступные команды:*
            
            📝 */book* [время] - записаться на игру. Можно указать время прихода в формате ЧЧ:ММ. Повторная \
            команда с указанием времени обновляет время прихода.
            ❌ */cancel* - отменить свою запись на игру
            📋 */list* - показать список записавшихся на игру
            🔓 */open* [время] - открыть запись на игру (для администраторов). Можно указать время начала игры в\
             формате ЧЧ:ММ (по умолчанию 18:00).
            🔒 */close* - закрыть запись на игру (для администраторов)
            🔢 */limit* [число] - изменить лимит игроков (для администраторов)
            ❓ */help* - показать эту справку
            
            В день игры запись открывается автоматически в 10:00""";

    public HelpCommandHandler(BookingService bookingService, SettingsService settingsService) {
        super(bookingService, settingsService);
    }

    @Nonnull
    @Override
    public String execute(Update update) {
        return HELP;
    }

    @Override
    public String getCommandName() {
        return "help";
    }
}
