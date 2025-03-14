package info.jab.demo.service;

import info.jab.demo.http.HttpClient;
import info.jab.demo.model.Fruit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FruitServiceTest {

    @Mock
    private HttpClient mockHttpClient;

    private FruitService fruitService;
    private static final String TEST_URL = "http://test.com/api/fruits";

    @BeforeEach
    void setUp() {
        fruitService = new FruitService(mockHttpClient, TEST_URL);
    }

    @Test
    void testGetAllFruits() throws IOException {
        // Arrange
        String jsonResponse = "[{\"name\":\"Apple\",\"color\":\"Red\",\"weight\":150.5}," +
                "{\"name\":\"Banana\",\"color\":\"Yellow\",\"weight\":120.0}]";
        
        when(mockHttpClient.get(TEST_URL)).thenReturn(jsonResponse);

        // Act
        List<Fruit> result = fruitService.getAllFruits();

        // Assert
        assertEquals(2, result.size());
        
        Fruit apple = result.get(0);
        assertEquals("Apple", apple.getName());
        assertEquals("Red", apple.getColor());
        assertEquals(150.5, apple.getWeight());
        
        Fruit banana = result.get(1);
        assertEquals("Banana", banana.getName());
        assertEquals("Yellow", banana.getColor());
        assertEquals(120.0, banana.getWeight());
        
        verify(mockHttpClient, times(1)).get(TEST_URL);
    }

    @Test
    void testParseFruitsEmptyArray() {
        // Arrange
        String jsonResponse = "[]";

        // Act
        List<Fruit> result = fruitService.parseFruits(jsonResponse);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void testParseFruitsInvalidJson() {
        // Arrange
        String jsonResponse = "This is not JSON";

        // Act & Assert
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            fruitService.parseFruits(jsonResponse);
        });
        
        assertTrue(exception.getMessage().contains("Failed to parse fruit data"));
    }

    @Test
    void testFindByName() throws IOException {
        // Arrange
        String jsonResponse = "[{\"name\":\"Apple\",\"color\":\"Red\",\"weight\":150.5}," +
                "{\"name\":\"Banana\",\"color\":\"Yellow\",\"weight\":120.0}]";
        
        when(mockHttpClient.get(TEST_URL)).thenReturn(jsonResponse);

        // Act
        Fruit result = fruitService.findByName("Banana");

        // Assert
        assertNotNull(result);
        assertEquals("Banana", result.getName());
        assertEquals("Yellow", result.getColor());
        assertEquals(120.0, result.getWeight());
        
        verify(mockHttpClient, times(1)).get(TEST_URL);
    }

    @Test
    void testFindByNameNotFound() throws IOException {
        // Arrange
        String jsonResponse = "[{\"name\":\"Apple\",\"color\":\"Red\",\"weight\":150.5}," +
                "{\"name\":\"Banana\",\"color\":\"Yellow\",\"weight\":120.0}]";
        
        when(mockHttpClient.get(TEST_URL)).thenReturn(jsonResponse);

        // Act
        Fruit result = fruitService.findByName("Orange");

        // Assert
        assertNull(result);
        verify(mockHttpClient, times(1)).get(TEST_URL);
    }
    
    @Test
    void testHttpClientException() throws IOException {
        // Arrange
        when(mockHttpClient.get(TEST_URL)).thenThrow(new IOException("Network error"));

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            fruitService.getAllFruits();
        });
        
        assertEquals("Network error", exception.getMessage());
        verify(mockHttpClient, times(1)).get(TEST_URL);
    }
} 