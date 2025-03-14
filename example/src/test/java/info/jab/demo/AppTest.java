package info.jab.demo;

import info.jab.demo.model.Fruit;
import info.jab.demo.service.FruitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test for simple App.
 */
@ExtendWith(MockitoExtension.class)
public class AppTest {

    @Mock
    private FruitService mockFruitService;

    private App app;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        app = new App();
        
        // Capture stdout for testing console output
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testRunWithMultipleFruits() throws IOException {
        // Arrange
        List<Fruit> fruits = Arrays.asList(
            new Fruit("Apple", "Red", 150.5),
            new Fruit("Banana", "Yellow", 120.0)
        );
        
        when(mockFruitService.getAllFruits()).thenReturn(fruits);
        when(mockFruitService.findByName("apple")).thenReturn(fruits.get(0));
        
        // Act
        app.run(mockFruitService);
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Found 2 fruits:"));
        assertTrue(output.contains("Apple (Red, 150.5g)"));
        assertTrue(output.contains("Banana (Yellow, 120.0g)"));
        assertTrue(output.contains("Found apple:"));
        
        verify(mockFruitService, times(1)).getAllFruits();
        verify(mockFruitService, times(1)).findByName("apple");
    }

    @Test
    void testRunWithNoFruits() throws IOException {
        // Arrange
        when(mockFruitService.getAllFruits()).thenReturn(Collections.emptyList());
        when(mockFruitService.findByName("apple")).thenReturn(null);
        
        // Act
        app.run(mockFruitService);
        
        // Assert
        String output = outputStream.toString();
        assertTrue(output.contains("Found 0 fruits:"));
        assertTrue(output.contains("No apple found"));
        
        verify(mockFruitService, times(1)).getAllFruits();
        verify(mockFruitService, times(1)).findByName("apple");
    }

    @Test
    void testRunThrowsIOException() throws IOException {
        // Arrange
        when(mockFruitService.getAllFruits()).thenThrow(new IOException("Network error"));
        
        // Act & Assert
        assertThrows(IOException.class, () -> app.run(mockFruitService));
        verify(mockFruitService, times(1)).getAllFruits();
        verify(mockFruitService, never()).findByName(anyString());
    }
    
    @Test
    void restoreSystemStreams() {
        // Always restore System.out after tests
        System.setOut(originalOut);
    }
}
