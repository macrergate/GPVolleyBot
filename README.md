# VolleyBot

Telegram-бот для организации записи на игры в волейбол в группе Telegram.

## Функциональность

- Запись на игры по расписанию (вторник 18:00, четверг 18:00, воскресенье 17:00)
- Отмена записи
- Просмотр списка записавшихся
- Изменение лимита игроков
- Автоматические уведомления об открытии записи (в 8:00 в день игры)
- Автоматическое закрытие записи в 21:00
- Ручное открытие и закрытие записи на игру

## Команды бота

- `/book [время]` - записаться на игру (опционально можно указать время прихода)
- `/cancel` - отменить свою запись
- `/list` - показать список записавшихся
- `/limit [число]` - изменить лимит игроков
- `/open` - вручную открыть запись на игру
- `/close` - вручную закрыть запись на игру

## Настройка и запуск

### Предварительные требования

- Java 21
- Maven
- Telegram Bot API Token (получить у [@BotFather](https://t.me/BotFather))

### Локальный запуск

1. Клонировать репозиторий:
   ```
   git clone https://github.com/yourusername/volleybot.git
   cd volleybot
   ```

2. Настроить переменные окружения:
   ```
   export BOT_TOKEN=your_bot_token_here
   export BOT_USERNAME=your_bot_username_here
   export CHAT_ID=your_chat_id_here
   ```

3. Собрать проект:
   ```
   mvn clean package
   ```

4. Запустить бот:
   ```
   java -jar target/volleybot-1.0.0.jar
   ```

### Деплой на Railway

1. Создать новый проект на [Railway](https://railway.app/)

2. Подключить репозиторий GitHub

3. Настроить переменные окружения в Railway:
   - `BOT_TOKEN` - токен бота Telegram
   - `BOT_USERNAME` - имя пользователя бота
   - `CHAT_ID` - ID чата, в котором будет работать бот

4. Railway автоматически обнаружит файл `railway.json` и настроит деплой

## Структура проекта

- `src/main/java/com/macrergate/` - исходный код
  - `bot/` - основной класс бота
  - `command/` - обработчики команд
  - `config/` - конфигурация приложения
  - `model/` - модели данных
  - `repository/` - репозитории для работы с базой данных
  - `service/` - сервисы
  - `scheduler/` - планировщик уведомлений
- `src/main/resources/` - ресурсы приложения
  - `application.properties` - настройки приложения
  - `schema.sql` - схема базы данных

## Технический стек

- Java 21
- Spring Boot
- Spring Data JDBC
- SQLite
- Telegram Bots API
