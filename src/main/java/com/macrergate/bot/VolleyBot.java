package com.macrergate.bot;

import com.macrergate.command.Command;
import com.macrergate.command.CommandRegistry;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс бота для Telegram
 */
@Slf4j
public class VolleyBot extends TelegramLongPollingBot {
    
    private final String botUsername;
    private final String chatId;
    private final CommandRegistry commandRegistry;

    public VolleyBot(@Value("${bot.token}") String botToken,
                     @Value("${bot.username}") String botUsername,
                     @Value("${bot.chatId}") String chatId,
                     CommandRegistry commandRegistry) {
        super(botToken);
        this.botUsername = botUsername;
        this.chatId = chatId;
        this.commandRegistry = commandRegistry;
    }
    
    /**
     * Метод вызывается после инициализации бина
     * Отправляет сообщение о том, что бот онлайн
     */
    @PostConstruct
    public void sendOnlineMessage() {
        sendMessageToGroup("Бот онлайн");
        
        // Регистрируем хук для отправки сообщения при завершении работы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                sendMessageToGroup("Бот офлайн");
            } catch (Exception e) {
                log.error("Ошибка при отправке сообщения о завершении работы", e);
            }
        }));
    }
    
    /**
     * Вспомогательный метод для отправки сообщения в группу
     */
    private void sendMessageToGroup(String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", text, e);
        }
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        
        String messageText = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        
        // Проверяем, что сообщение начинается с "/"
        if (!messageText.startsWith("/")) {
            return;
        }
        
        // Извлекаем имя команды (без слеша)
        String[] parts = messageText.split(" ", 2);
        String commandName = parts[0].substring(1); // Убираем слеш
        
        // Проверяем, существует ли такая команда
        if (!commandRegistry.hasCommand(commandName)) {
            return;
        }
        
        // Получаем команду и выполняем ее
        Command command = commandRegistry.getCommand(commandName);
        String response = command.execute(update);
        
        // Отправляем ответ пользователю
        if (response != null && !response.isEmpty()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(response);
            
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                // Обработка ошибки
            }
        }
    }
}
