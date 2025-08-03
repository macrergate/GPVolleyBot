package com.macrergate.command;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерфейс для обработчиков команд бота
 */
public interface Command {
    
    /**
     * Выполняет команду и возвращает текстовый ответ
     * 
     * @param update Объект обновления от Telegram
     * @return Текстовый ответ для отправки пользователю
     */
    String execute(Update update);
    
    /**
     * Возвращает имя команды (без слеша)
     * 
     * @return Имя команды
     */
    String getCommandName();
}
