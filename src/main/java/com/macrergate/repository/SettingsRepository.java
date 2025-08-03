package com.macrergate.repository;

import java.util.Optional;

import org.komamitsu.spring.data.sqlite.SqliteRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.stereotype.Repository;

import com.macrergate.model.Settings;

@Repository
public interface SettingsRepository extends SqliteRepository<Settings, Long> {
    @Query("SELECT * FROM settings WHERE id = 1")
    Optional<Settings> findSettings();
}
