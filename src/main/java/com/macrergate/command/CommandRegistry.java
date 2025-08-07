package com.macrergate.command;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Реестр всех доступных команд бота
 */
@Component
@RequiredArgsConstructor
public class CommandRegistry {
    
    private final List<Command> commands;
    private final Map<String, Command> commandMap = new HashMap<>();
    
    /**
     * Инициализирует реестр команд
     */
    @PostConstruct
    public void init() {
        for (Command command : commands) {
            commandMap.put(command.getCommandName(), command);
        }
    }

    private static String normalize(String commandName) {
        return commandName.toLowerCase();
    }
    
    /**
     * Возвращает команду по ее имени
     *
     * @param commandName Имя команды (без слеша)
     * @return Команда или null, если команда не найдена
     */
    public Command getCommand(String commandName) {
        return commandMap.get(normalize(commandName));
    }

    /**
     * Проверяет, существует ли команда с указанным именем
     *
     * @param commandName Имя команды (без слеша)
     * @return true, если команда существует, иначе false
     */
    public boolean hasCommand(String commandName) {
        return commandMap.containsKey(normalize(commandName));
    }

    /**
     * Возвращает список всех доступных команд
     * 
     * @return Список команд
     */
    public List<Command> getAllCommands() {
        return commands;
    }
}
