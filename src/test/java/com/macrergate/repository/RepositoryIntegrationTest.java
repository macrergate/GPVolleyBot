package com.macrergate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.macrergate.spring.data.sqlite.SqliteJdbcConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.context.annotation.Import;

import com.macrergate.model.Booking;
import com.macrergate.model.Settings;

@DataJdbcTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Sql(scripts = {"/schema-test.sql"})
@Import(SqliteJdbcConfiguration.class)
public class RepositoryIntegrationTest {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void testSettingsRepository() {
        // Arrange
        Settings settings = new Settings();
        settings.setId(1L);
        settings.setPlayerLimit(21);
        settings.setCurrentGameDay("Вторник");
        settings.setCurrentGameTimeAsLocalTime(LocalTime.of(18, 0));
        settings.setCurrentGameDateAsLocalDate(LocalDate.now());

        // Act
        Settings savedSettings = settingsRepository.save(settings);
        Optional<Settings> foundSettings = settingsRepository.findSettings();

        // Assert
        assertThat(foundSettings).isPresent();
        assertThat(foundSettings.get().getId()).isEqualTo(savedSettings.getId());
        assertThat(foundSettings.get().getPlayerLimit()).isEqualTo(21);
        assertThat(foundSettings.get().getCurrentGameDay()).isEqualTo("Вторник");
        assertThat(foundSettings.get().getCurrentGameTimeAsLocalTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(foundSettings.get().getCurrentGameDateAsLocalDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void testBookingRepository() {
        // Arrange
        Booking booking1 = new Booking();
        booking1.setUserId("user1");
        booking1.setDisplayName("User 1");
        booking1.setBookingTimeAsLocalDateTime(LocalDateTime.now());
        booking1.setArrivalTimeAsLocalTime(LocalTime.of(18, 30));

        Booking booking2 = new Booking();
        booking2.setUserId("user2");
        booking2.setDisplayName("User 2");
        booking2.setBookingTimeAsLocalDateTime(LocalDateTime.now().plusMinutes(5));
        booking2.setArrivalTimeAsLocalTime(null);

        // Act
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        List<Booking> allBookings = bookingRepository.findAllBookings();
        Optional<Booking> foundBooking = bookingRepository.findByUserId("user1");

        // Assert
        assertThat(allBookings).hasSize(2);
        assertThat(foundBooking).isPresent();
        assertThat(foundBooking.get().getDisplayName()).isEqualTo("User 1");
        assertThat(foundBooking.get().getArrivalTimeAsLocalTime()).isEqualTo(LocalTime.of(18, 30));
    }

    @Test
    void testDeleteBooking() {
        // Arrange
        Booking booking = new Booking();
        booking.setUserId("user1");
        booking.setDisplayName("User 1");
        booking.setBookingTimeAsLocalDateTime(LocalDateTime.now());
        bookingRepository.save(booking);

        // Act
        bookingRepository.deleteByUserId("user1");
        Optional<Booking> foundBooking = bookingRepository.findByUserId("user1");

        // Assert
        assertThat(foundBooking).isEmpty();
    }

    @Test
    void testDeleteAllBookings() {
        // Arrange
        Booking booking1 = new Booking();
        booking1.setUserId("user1");
        booking1.setDisplayName("User 1");
        booking1.setBookingTimeAsLocalDateTime(LocalDateTime.now());

        Booking booking2 = new Booking();
        booking2.setUserId("user2");
        booking2.setDisplayName("User 2");
        booking2.setBookingTimeAsLocalDateTime(LocalDateTime.now().plusMinutes(5));

        bookingRepository.save(booking1);
        bookingRepository.save(booking2);

        // Act
        bookingRepository.deleteAllBookings();
        List<Booking> allBookings = bookingRepository.findAllBookings();

        // Assert
        assertThat(allBookings).isEmpty();
    }
}
