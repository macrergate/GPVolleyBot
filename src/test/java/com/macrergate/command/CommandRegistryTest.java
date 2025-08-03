package com.macrergate.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommandRegistryTest {

    @Mock
    private BookCommandHandler bookCommandHandler;
    
    @Mock
    private CancelCommandHandler cancelCommandHandler;
    
    @Mock
    private ListCommandHandler listCommandHandler;
    
    @Mock
    private LimitCommandHandler limitCommandHandler;
    
    private CommandRegistry commandRegistry;
    
    @BeforeEach
    void setUp() {
        // Настройка моков команд
        when(bookCommandHandler.getCommandName()).thenReturn("book");
        when(cancelCommandHandler.getCommandName()).thenReturn("cancel");
        when(listCommandHandler.getCommandName()).thenReturn("list");
        when(limitCommandHandler.getCommandName()).thenReturn("limit");
        
        // Создаем список команд
        List<Command> commands = Arrays.asList(
            bookCommandHandler,
            cancelCommandHandler,
            listCommandHandler,
            limitCommandHandler
        );
        
        // Создаем реестр команд
        commandRegistry = new CommandRegistry(commands);
        commandRegistry.init();
    }
    
    @Test
    void testGetCommand_Exists() {
        // Act
        Command command = commandRegistry.getCommand("book");
        
        // Assert
        assertThat(command).isEqualTo(bookCommandHandler);
    }
    
    @Test
    void testGetCommand_NotExists() {
        // Act
        Command command = commandRegistry.getCommand("unknown");
        
        // Assert
        assertThat(command).isNull();
    }
    
    @Test
    void testHasCommand_Exists() {
        // Act
        boolean hasCommand = commandRegistry.hasCommand("cancel");
        
        // Assert
        assertThat(hasCommand).isTrue();
    }
    
    @Test
    void testHasCommand_NotExists() {
        // Act
        boolean hasCommand = commandRegistry.hasCommand("unknown");
        
        // Assert
        assertThat(hasCommand).isFalse();
    }
    
    @Test
    void testGetAllCommands() {
        // Act
        List<Command> allCommands = commandRegistry.getAllCommands();
        
        // Assert
        assertThat(allCommands).hasSize(4);
        assertThat(allCommands).contains(
            bookCommandHandler,
            cancelCommandHandler,
            listCommandHandler,
            limitCommandHandler
        );
    }
}
