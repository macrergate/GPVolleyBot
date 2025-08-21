package com.macrergate.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.macrergate.model.Booking;
import com.macrergate.model.Settings;
import com.macrergate.repository.BookingRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final SettingsService settingsService;

    public BookingResult bookGame(String userId, String displayName, @Nullable LocalTime arrivalTime) {
        if (!settingsService.isBookingOpen()) {
            return BookingResult.BOOKING_CLOSED;
        }

        Optional<Booking> existingBooking = bookingRepository.findByUserId(userId);
        if (existingBooking.isPresent()) {
            // Если пользователь уже записан и указано время прихода, обновляем его
            if (arrivalTime != null) {
                Booking booking = existingBooking.get();
                booking.setArrivalTimeAsLocalTime(arrivalTime);
                bookingRepository.save(booking);
                return BookingResult.TIME_UPDATED;
            }
            return BookingResult.ALREADY_BOOKED;
        }

        if (isPlayerLimitReached()) {
            return BookingResult.PLAYER_LIMIT_REACHED;
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

    private boolean isPlayerLimitReached() {
        Settings settings = settingsService.getSettings();
        int bookingsCount = bookingRepository.findBookingsCount();
        return bookingsCount >= settings.getPlayerLimit();
    }

    public enum BookingResult {
        SUCCESS,
        PLAYER_LIMIT_REACHED,
        ALREADY_BOOKED,
        BOOKING_CLOSED,
        TIME_UPDATED
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
