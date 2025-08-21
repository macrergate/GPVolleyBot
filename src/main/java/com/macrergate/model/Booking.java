package com.macrergate.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("bookings")
public class Booking {
    @Id
    private Long id;
    private String userId;
    private String displayName;
    private String bookingTime;
    private String arrivalTime;

    public void setBookingTimeAsLocalDateTime(LocalDateTime localDateTime) {
        this.bookingTime = localDateTime != null ? localDateTime.toString() : null;
    }

    public Optional<LocalTime> getArrivalTimeAsLocalTime() {
        return Optional.ofNullable(arrivalTime).map(LocalTime::parse);
    }

    public void setArrivalTimeAsLocalTime(@Nullable LocalTime localTime) {
        this.arrivalTime = localTime != null ? localTime.toString() : null;
    }
}
