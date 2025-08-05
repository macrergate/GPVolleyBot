package com.macrergate.model;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("settings")
public class Settings {
    public static final int DEFAULT_PLAYER_LIMIT = 18;
    @Id
    private Long id = 1L; // Всегда 1
    private int playerLimit = DEFAULT_PLAYER_LIMIT;
    @Deprecated
    private String currentGameDay;
    private String currentGameTime;
    private String currentGameDate;
    private boolean bookingOpen = false; // Флаг, указывающий открыта ли запись на игру
    
    // Геттеры и сеттеры для работы с Java-типами
    public LocalTime getCurrentGameTimeAsLocalTime() {
        return currentGameTime != null ? LocalTime.parse(currentGameTime) : null;
    }
    
    public void setCurrentGameTimeAsLocalTime(LocalTime localTime) {
        this.currentGameTime = localTime != null ? localTime.toString() : null;
    }
    
    public LocalDate getCurrentGameDateAsLocalDate() {
        return currentGameDate != null ? LocalDate.parse(currentGameDate) : null;
    }
    
    public void setCurrentGameDateAsLocalDate(LocalDate localDate) {
        this.currentGameDate = localDate != null ? localDate.toString() : null;
    }
}
