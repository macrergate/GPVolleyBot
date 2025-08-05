package com.macrergate.bot;

import com.macrergate.command.BookCommandHandler;
import com.macrergate.command.CancelCommandHandler;
import com.macrergate.command.CommandRegistry;
import com.macrergate.command.LimitCommandHandler;
import com.macrergate.command.ListCommandHandler;
import com.macrergate.config.BotProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тесты для класса VolleyBot
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class VolleyBotTest {

    @Mock
    private CommandRegistry commandRegistry;
    
    @Mock
    private BookCommandHandler bookCommandHandler;
    
    @Mock
    private CancelCommandHandler cancelCommandHandler;
    
    @Mock
    private ListCommandHandler listCommandHandler;
    
    @Mock
    private LimitCommandHandler limitCommandHandler;

    private VolleyBot volleyBot;
    
    private Update update;
    private Message message;
    private final String botUsername = "TestBot";
    @Mock
    private BotProperties botProperties;

    @BeforeEach
    void setUp() {
        // Настраиваем мок BotProperties
        String botToken = "test_token";
        when(botProperties.getToken()).thenReturn(botToken);
        when(botProperties.getUsername()).thenReturn(botUsername);
        String chatId = "test_chat_id";
        when(botProperties.getChatId()).thenReturn(chatId);
        String adminChatId = "test_admin_chat_id";
        when(botProperties.getAdminChatId()).thenReturn(adminChatId);
        
        // Создаем экземпляр бота с тестовыми параметрами
        volleyBot = new VolleyBot(botProperties, commandRegistry, listCommandHandler);
        
        // Настройка объектов Telegram API
        update = new Update();
        message = new Message();
        User user = new User();
        Chat chat = new Chat();
        
        user.setId(123456789L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserName("testuser");
        
        chat.setId(987654321L);
        
        message.setFrom(user);
        message.setChat(chat);
        
        update.setMessage(message);
        
        // Настройка моков команд
        when(bookCommandHandler.getCommandName()).thenReturn("book");
        when(cancelCommandHandler.getCommandName()).thenReturn("cancel");
        when(listCommandHandler.getCommandName()).thenReturn("list");
        when(limitCommandHandler.getCommandName()).thenReturn("limit");
    }
    
    @Test
    void testGetBotUsername() {
        // Act
        String result = volleyBot.getBotUsername();
        
        // Assert
        assertThat(result).isEqualTo(botUsername);
    }
    
    @Test
    void testOnUpdateReceived_BookCommand() {
        // Arrange
        message.setText("/book");
        when(commandRegistry.hasCommand("book")).thenReturn(true);
        when(commandRegistry.getCommand("book")).thenReturn(bookCommandHandler);
        when(bookCommandHandler.execute(any(Update.class))).thenReturn("Test response");
        
        // Act
        volleyBot.onUpdateReceived(update);
        
        // Assert
        verify(commandRegistry, times(1)).hasCommand("book");
        verify(commandRegistry, times(1)).getCommand("book");
        verify(bookCommandHandler, times(1)).execute(update);
    }
    
    @Test
    void testOnUpdateReceived_CancelCommand() {
        // Arrange
        message.setText("/cancel");
        when(commandRegistry.hasCommand("cancel")).thenReturn(true);
        when(commandRegistry.getCommand("cancel")).thenReturn(cancelCommandHandler);
        when(cancelCommandHandler.execute(any(Update.class))).thenReturn("Test response");
        
        // Act
        volleyBot.onUpdateReceived(update);
        
        // Assert
        verify(commandRegistry, times(1)).hasCommand("cancel");
        verify(commandRegistry, times(1)).getCommand("cancel");
        verify(cancelCommandHandler, times(1)).execute(update);
    }
    
    @Test
    void testOnUpdateReceived_ListCommand() {
        // Arrange
        message.setText("/list");
        when(commandRegistry.hasCommand("list")).thenReturn(true);
        when(commandRegistry.getCommand("list")).thenReturn(listCommandHandler);
        when(listCommandHandler.execute(any(Update.class))).thenReturn("Test response");
        
        // Act
        volleyBot.onUpdateReceived(update);
        
        // Assert
        verify(commandRegistry, times(1)).hasCommand("list");
        verify(commandRegistry, times(1)).getCommand("list");
        verify(listCommandHandler, times(1)).execute(update);
    }
    
    @Test
    void testOnUpdateReceived_LimitCommand() {
        // Arrange
        message.setText("/limit 25");
        when(commandRegistry.hasCommand("limit")).thenReturn(true);
        when(commandRegistry.getCommand("limit")).thenReturn(limitCommandHandler);
        when(limitCommandHandler.execute(any(Update.class))).thenReturn("Test response");
        
        // Act
        volleyBot.onUpdateReceived(update);
        
        // Assert
        verify(commandRegistry, times(1)).hasCommand("limit");
        verify(commandRegistry, times(1)).getCommand("limit");
        verify(limitCommandHandler, times(1)).execute(update);
    }
    
    @Test
    void testOnUpdateReceived_NullMessage() {
        // Arrange
        update.setMessage(null);
        
        // Act
        volleyBot.onUpdateReceived(update);
        
        // Assert - проверяем, что никакие методы не вызываются
        verify(commandRegistry, times(0)).hasCommand(any());
        verify(commandRegistry, times(0)).getCommand(any());
    }
    
    @Test
    void testOnUpdateReceived_UnknownCommand() {
        // Arrange
        message.setText("/unknown");
        when(commandRegistry.hasCommand("unknown")).thenReturn(false);
        
        // Act
        volleyBot.onUpdateReceived(update);
        
        // Assert
        verify(commandRegistry, times(1)).hasCommand("unknown");
        verify(commandRegistry, times(0)).getCommand(any());
    }
    
    @Test
    void testOnUpdateReceived_NotCommand() {
        // Arrange
        message.setText("This is not a command");
        
        // Act
        volleyBot.onUpdateReceived(update);
        
        // Assert
        verify(commandRegistry, times(0)).hasCommand(any());
        verify(commandRegistry, times(0)).getCommand(any());
    }
    
    @Test
    void testSendOnlineMessage() throws Exception {
        // Arrange
        VolleyBot spyBot = spy(volleyBot);
        doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));
        when(listCommandHandler.execute()).thenReturn("Test response");

        // Act
        spyBot.sendOnlineMessage();
        
        // Assert - проверяем, что метод execute был вызван с сообщением "Бот онлайн"
        verify(spyBot, times(1)).execute(argThat((SendMessage message) ->
                message.getChatId().equals(botProperties.getAdminChatId()) &&
            message.getText().equals("Бот онлайн")
        ));
    }
    
    @Test
    void testSendMessageToGroup() throws Exception {
        // Arrange
        VolleyBot spyBot = spy(volleyBot);
        doAnswer(invocation -> null).when(spyBot).execute(any(SendMessage.class));
        String testMessage = "Тестовое сообщение";
        String testChatId = "Тестовый чат идентификатор";

        // Act
        spyBot.sendMessageToGroup(testChatId, testMessage);

        // Assert - проверяем, что метод execute был вызван с правильными параметрами
        verify(spyBot, times(1)).execute(argThat((SendMessage message) ->
                message.getChatId().equals(testChatId) &&
            message.getText().equals(testMessage)
        ));
    }
}
