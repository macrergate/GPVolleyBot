package com.macrergate.command;

import java.util.ArrayList;
import java.util.List;

import com.macrergate.model.Booking;
import com.macrergate.model.Settings;
import com.macrergate.service.BookingService;
import com.macrergate.service.SettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListCommandHandlerTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private ListCommandHandler listCommandHandler;

    private Settings settings;
    private List<Booking> bookings;
    private Update update;

    @BeforeEach
    void setUp() {
        // Настройка тестовых данных
        settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);

        bookings = new ArrayList<>();
        
        // Добавляем несколько записей
        Booking booking1 = new Booking();
        booking1.setUserId("user1");
        booking1.setDisplayName("User 1");
        bookings.add(booking1);
        
        Booking booking2 = new Booking();
        booking2.setUserId("user2");
        booking2.setDisplayName("User 2");
        bookings.add(booking2);
        
        // Настройка объектов Telegram API
        update = new Update();
        Message message = new Message();
        User user = new User();
        Chat chat = new Chat();
        
        user.setId(123456789L);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUserName("testuser");
        
        chat.setId(987654321L);
        
        message.setFrom(user);
        message.setChat(chat);
        message.setText("/list");
        
        update.setMessage(message);
    }

    @Test
    void testExecute_WithBookings() {
        // Arrange
        when(bookingService.getAllBookings()).thenReturn(bookings);
        when(settingsService.getSettings()).thenReturn(settings);

        // Act
        String response = listCommandHandler.execute(update);

        // Assert
        assertThat(response).contains("Список записавшихся на игру");
        assertThat(response).contains("User 1");
        assertThat(response).contains("User 2");
        assertThat(response).contains("Всего: 2/" + Settings.DEFAULT_PLAYER_LIMIT);
    }
    
    @Test
    void testExecute_NoBookings() {
        // Arrange
        when(bookingService.getAllBookings()).thenReturn(new ArrayList<>());

        // Act
        String response = listCommandHandler.execute(update);

        // Assert
        assertThat(response).isEqualTo("На сегодня нет записей.");
    }
    
    @Test
    void testGetCommandName() {
        // Act
        String commandName = listCommandHandler.getCommandName();
        
        // Assert
        assertThat(commandName).isEqualTo("list");
    }
}
