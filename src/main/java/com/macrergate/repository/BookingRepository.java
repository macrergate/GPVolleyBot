package com.macrergate.repository;

import java.util.List;
import java.util.Optional;

import com.macrergate.model.Booking;
import org.komamitsu.spring.data.sqlite.SqliteRepository;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends SqliteRepository<Booking, Long> {
    @Query("SELECT * FROM bookings ORDER BY booking_time")
    List<Booking> findAllBookings();

    @Query("SELECT count(*) FROM bookings")
    int findBookingsCount();

    @Query("SELECT * FROM bookings WHERE user_id = :userId")
    Optional<Booking> findByUserId(String userId);
    
    @Query("DELETE FROM bookings WHERE user_id = :userId")
    @Modifying
    void deleteByUserId(String userId);
    
    default void deleteAllBookings() {
        deleteAll();
    }
}
