package com.macrergate.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.macrergate.model.Booking;
import com.macrergate.model.Settings;
import com.macrergate.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    
    @Mock
    private SettingsService settingsService;
    
    @InjectMocks
    private BookingService bookingService;
    
    private Booking booking1;
    private Booking booking2;
    private Settings settings;
    
    @BeforeEach
    void setUp() {
        booking1 = new Booking();
        booking1.setId(1L);
        booking1.setUserId("user1");
        booking1.setDisplayName("User 1");
        booking1.setBookingTimeAsLocalDateTime(LocalDateTime.now());
        booking1.setArrivalTimeAsLocalTime(LocalTime.of(18, 30));
        
        booking2 = new Booking();
        booking2.setId(2L);
        booking2.setUserId("user2");
        booking2.setDisplayName("User 2");
        booking2.setBookingTimeAsLocalDateTime(LocalDateTime.now().plusMinutes(5));
        booking2.setArrivalTimeAsLocalTime(null);
        
        settings = new Settings();
        settings.setPlayerLimit(Settings.DEFAULT_PLAYER_LIMIT);
    }
    
    @Test
    void testGetAllBookings() {
        // Arrange
        when(bookingRepository.findAllBookings()).thenReturn(Arrays.asList(booking1, booking2));
        
        // Act
        List<Booking> bookings = bookingService.getAllBookings();
        
        // Assert
        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getUserId()).isEqualTo("user1");
        assertThat(bookings.get(1).getUserId()).isEqualTo("user2");
    }
    
    @Test
    void testBookGame() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        when(bookingRepository.findByUserId("user1")).thenReturn(Optional.empty());
        when(bookingRepository.findAllBookings()).thenReturn(Collections.singletonList(booking2));
        when(settingsService.getSettings()).thenReturn(settings);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);
        
        // Act
        BookingService.BookingResult result = bookingService.bookGame("user1", "User 1", LocalTime.of(18, 30));
        
        // Assert
        assertThat(result).isEqualTo(BookingService.BookingResult.SUCCESS);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
    
    @Test
    void testBookGameWhenAlreadyExists() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        when(bookingRepository.findByUserId("user1")).thenReturn(Optional.of(booking1));
        when(settingsService.getSettings()).thenReturn(settings);

        // Act - без указания времени
        BookingService.BookingResult result = bookingService.bookGame("user1", "User 1", null);
        
        // Assert
        assertThat(result).isEqualTo(BookingService.BookingResult.ALREADY_BOOKED);
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }

    @Test
    void testUpdateArrivalTime() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        when(settingsService.getSettings()).thenReturn(settings);
        when(bookingRepository.findByUserId("user1")).thenReturn(Optional.of(booking1));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking1);

        // Act - с указанием нового времени
        LocalTime newTime = LocalTime.of(19, 0);
        BookingService.BookingResult result = bookingService.bookGame("user1", "User 1", newTime);

        // Assert
        assertThat(result).isEqualTo(BookingService.BookingResult.TIME_UPDATED);
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }
    
    @Test
    void testBookGameWhenBookingClosed() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(false);
        
        // Act
        BookingService.BookingResult result = bookingService.bookGame("user1", "User 1", LocalTime.of(18, 30));
        
        // Assert
        assertThat(result).isEqualTo(BookingService.BookingResult.BOOKING_CLOSED);
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }
    
    @Test
    void testBookGameWhenLimitReached() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        // Удаляем ненужную заглушку: when(bookingRepository.findByUserId("user3")).thenReturn(Optional.empty());
        when(bookingRepository.findAllBookings()).thenReturn(Arrays.asList(booking1, booking2));
        when(settingsService.getSettings()).thenReturn(settings);
        settings.setPlayerLimit(2); // Limit is 2, and we already have 2 bookings
        
        // Act
        BookingService.BookingResult result = bookingService.bookGame("user3", "User 3", LocalTime.of(18, 30));
        
        // Assert
        assertThat(result).isEqualTo(BookingService.BookingResult.PLAYER_LIMIT_REACHED);
        verify(bookingRepository, times(0)).save(any(Booking.class));
    }
    
    
    @Test
    void testCancelBooking() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        when(bookingRepository.findByUserId("user1")).thenReturn(Optional.of(booking1));
        
        // Act
        boolean result = bookingService.cancelBooking("user1");
        
        // Assert
        assertThat(result).isTrue();
        verify(bookingRepository, times(1)).deleteByUserId("user1");
    }
    
    @Test
    void testCancelBookingWhenBookingClosed() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(false);
        
        // Act
        boolean result = bookingService.cancelBooking("user1");
        
        // Assert
        assertThat(result).isFalse();
        verify(bookingRepository, times(0)).deleteByUserId(anyString());
    }
    
    @Test
    void testCancelBookingWhenNotExists() {
        // Arrange
        when(settingsService.isBookingOpen()).thenReturn(true);
        when(bookingRepository.findByUserId("user3")).thenReturn(Optional.empty());
        
        // Act
        boolean result = bookingService.cancelBooking("user3");
        
        // Assert
        assertThat(result).isFalse();
        verify(bookingRepository, times(0)).deleteByUserId(anyString());
    }
    
    @Test
    void testClearAllBookings() {
        // Act
        bookingService.clearAllBookings();
        
        // Assert
        verify(bookingRepository, times(1)).deleteAllBookings();
    }
}
