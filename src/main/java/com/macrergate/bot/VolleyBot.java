package com.macrergate.bot;

import com.macrergate.command.Command;
import com.macrergate.command.CommandRegistry;
import com.macrergate.command.ListCommandHandler;
import com.macrergate.config.BotProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Основной класс бота для Telegram
 */
@Component
@Slf4j
public class VolleyBot extends TelegramLongPollingBot {

    private final BotProperties botProperties;
    private final CommandRegistry commandRegistry;
    private final ListCommandHandler listCommandHandler;

    public VolleyBot(BotProperties botProperties,
                     CommandRegistry commandRegistry,
                     ListCommandHandler listCommandHandler
    ) {
        super(botProperties.getToken());
        this.botProperties = botProperties;
        this.commandRegistry = commandRegistry;
        this.listCommandHandler = listCommandHandler;
    }

    /**
     * Метод вызывается после инициализации бина
     * Отправляет сообщение о том, что бот онлайн
     */
    @PostConstruct
    public void sendOnlineMessage() {
        sendMessageToAdmin("Бот онлайн", true);
        sendMessageToAdmin(listCommandHandler.execute(), true);
        // Регистрируем хук для отправки сообщения при завершении работы
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                sendMessageToAdmin("Бот офлайн", true);
            } catch (Exception e) {
                log.error("Ошибка при отправке сообщения о завершении работы", e);
            }
        }));
    }

    /**
     * Метод для отправки сообщения в группу
     *
     * @param text   текст сообщения
     * @param chatId идентификатор групы
     */
    public void sendMessageToGroup(long chatId, String text) {
        sendMessageToGroup(chatId, text, true);
    }

    /**
     * Метод для отправки сообщения в группу без звука
     *
     * @param text   текст сообщения
     * @param chatId идентификатор групы
     */
    @SuppressWarnings("unused")
    public void sendLoudMessageToGroup(long chatId, String text) {
        sendMessageToGroup(chatId, text, false);
    }

    /**
     * Метод для отправки сообщения админу
     *
     * @param text текст сообщения
     */
    public void sendMessageToAdmin(String text, boolean silent) {
        sendMessageToGroup(botProperties.getAdminChatId(), text, silent);
    }

    /**
     * Метод для отправки сообщения без звука в группу
     */
    private void sendMessageToGroup(long chatId, String text, boolean silent) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if (silent) {
            message.disableNotification();
        }

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", text, e);
        }
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String messageText = update.getMessage().getText();

        // Проверяем, что сообщение начинается с "/"
        if (!messageText.startsWith("/")) {
            return;
        }

        // Извлекаем имя команды (без слеша)
        String[] parts = messageText.split(" ", 2);

        String commandName = parts[0].substring(1); // Убираем слеш и пробелы


        boolean botMention = false;
        if (commandName.endsWith("@" + botProperties.getUsername())) {
            botMention = true;
            commandName = commandName.substring(0, commandName.length() - botProperties.getUsername().length() - 1);
        }

        // Проверяем, существует ли такая команда
        if (!commandRegistry.hasCommand(commandName)) {
            if (botMention) {
                sendMessageToGroup(
                        update.getMessage().getChatId(), "❌ Неизвестная команда: '" + commandName + "'!"
                );
            }
            return;
        }

        // Получаем команду и выполняем ее
        Command command = commandRegistry.getCommand(commandName);
        String response = command.execute(update);

        // Отправляем ответ пользователю
        if (response != null && !response.isEmpty()) {
            sendMessageToGroup(update.getMessage().getChatId(), response);
        }
    }

    public void sendLoudMessageToMainGroup(String message) {
        sendLoudMessageToGroup(botProperties.getChatId(), message);
    }
}
