package com.macrergate.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.macrergate.model.Booking;
import com.macrergate.model.Settings;
import com.macrergate.repository.BookingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SettingsService settingsService;
    
    public enum BookingResult {
        SUCCESS,
        PLAYER_LIMIT_REACHED,
        ALREADY_BOOKED,
        BOOKING_CLOSED
    }
    
    public BookingResult bookGame(String userId, String displayName, LocalTime arrivalTime) {
        if (!settingsService.isBookingOpen()) {
            return BookingResult.BOOKING_CLOSED;
        }
        
        Settings settings = settingsService.getSettings();
        List<Booking> bookings = bookingRepository.findAllBookings();
        if (bookings.size() >= settings.getPlayerLimit()) {
            return BookingResult.PLAYER_LIMIT_REACHED;
        }
        
        Optional<Booking> existingBooking = bookingRepository.findByUserId(userId);
        if (existingBooking.isPresent()) {
            return BookingResult.ALREADY_BOOKED;
        }
        
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setDisplayName(displayName);
        booking.setBookingTimeAsLocalDateTime(LocalDateTime.now());
        if (arrivalTime != null) {
            booking.setArrivalTimeAsLocalTime(arrivalTime);
        }
        
        bookingRepository.save(booking);
        return BookingResult.SUCCESS;
    }
    
    public boolean cancelBooking(String userId) {
        if (!settingsService.isBookingOpen()) {
            return false; // Запись закрыта
        }
        
        Optional<Booking> bookingOpt = bookingRepository.findByUserId(userId);
        if (bookingOpt.isEmpty()) {
            return false; // Пользователь не записан
        }
        
        bookingRepository.deleteByUserId(userId);
        return true;
    }
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllBookings();
    }
    
    public void clearAllBookings() {
        bookingRepository.deleteAllBookings();
    }
}
