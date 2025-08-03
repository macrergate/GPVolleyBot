package com.macrergate.model;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table("bookings")
public class Booking {
    @Id
    private Long id;
    private String userId;
    private String displayName;
    private String bookingTime;
    private String arrivalTime;
    
    // Геттеры и сеттеры для работы с Java-типами
    public LocalDateTime getBookingTimeAsLocalDateTime() {
        return bookingTime != null ? LocalDateTime.parse(bookingTime) : null;
    }
    
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
