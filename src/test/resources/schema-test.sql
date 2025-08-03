-- Таблица настроек
CREATE TABLE IF NOT EXISTS settings (
    id INTEGER PRIMARY KEY,
    player_limit INTEGER NOT NULL DEFAULT 21,
    current_game_day VARCHAR(20),
    current_game_time VARCHAR(10),
    current_game_date VARCHAR(10),
    booking_open INTEGER DEFAULT 0
);

-- Таблица записей на игру
CREATE TABLE IF NOT EXISTS bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id VARCHAR(50) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    booking_time VARCHAR(30) NOT NULL,
    arrival_time VARCHAR(10)
);

-- Очистка таблиц перед каждым тестом
DELETE FROM settings;
DELETE FROM bookings;
-- Вставка начальных настроек
INSERT INTO settings (id, player_limit) VALUES (1, 21);
