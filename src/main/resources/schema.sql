-- Таблица настроек
CREATE TABLE IF NOT EXISTS settings (
    id INTEGER PRIMARY KEY CHECK (id = 1), -- Только одна запись
    player_limit INTEGER NOT NULL,
    current_game_day TEXT, -- День текущей игры (например, "Вторник")
    current_game_time TEXT, -- Время текущей игры (например, "18:00")
    current_game_date TEXT, -- Дата текущей игры (например, "2023-08-02")
    booking_open INTEGER DEFAULT 0 -- Флаг, указывающий открыта ли запись на игру (0 - закрыта, 1 - открыта)
);

-- Таблица записей на игру
CREATE TABLE IF NOT EXISTS bookings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id TEXT NOT NULL,
    display_name TEXT NOT NULL,
    booking_time TEXT NOT NULL,
    arrival_time TEXT
);

-- Вставка начальных настроек, если таблица пуста
INSERT OR IGNORE INTO settings (id, player_limit) VALUES (1, 18);
