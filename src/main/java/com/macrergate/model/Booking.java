package com.macrergate.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

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
    
    public LocalTime getArrivalTimeAsLocalTime() {
        return arrivalTime != null ? LocalTime.parse(arrivalTime) : null;
    }
    
    public void setArrivalTimeAsLocalTime(LocalTime localTime) {
        this.arrivalTime = localTime != null ? localTime.toString() : null;
    }
}
