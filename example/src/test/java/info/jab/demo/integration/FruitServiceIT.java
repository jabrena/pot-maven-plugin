package info.jab.demo.integration;

import info.jab.demo.http.HttpClient;
import info.jab.demo.model.Fruit;
import info.jab.demo.service.FruitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Tag("integration")
class FruitServiceIT {

    private static final int PORT = 8089;
    private static final String HOST = "localhost";
    private static final String FRUITS_ENDPOINT = "/api/fruits";
    private WireMockServer wireMockServer;
    
    private HttpClient httpClient;
    private FruitService fruitService;

    @BeforeEach
    void setUp() {
        // Configure WireMock server
        wireMockServer = new WireMockServer(wireMockConfig().port(PORT));
        wireMockServer.start();
        WireMock.configureFor(HOST, PORT);
        
        // Create service with real HttpClient
        httpClient = new HttpClient();
        String serviceUrl = "http://" + HOST + ":" + PORT + FRUITS_ENDPOINT;
        fruitService = new FruitService(httpClient, serviceUrl);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testGetAllFruitsSuccessful() throws IOException {
        // Arrange - Configure WireMock to return a successful response
        stubFor(get(urlEqualTo(FRUITS_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"Apple\",\"color\":\"Red\",\"weight\":150.5}," +
                                "{\"name\":\"Banana\",\"color\":\"Yellow\",\"weight\":120.0}]")));

        // Act
        List<Fruit> fruits = fruitService.getAllFruits();

        // Assert
        assertEquals(2, fruits.size());
        
        // Verify the first fruit
        Fruit apple = fruits.get(0);
        assertEquals("Apple", apple.getName());
        assertEquals("Red", apple.getColor());
        assertEquals(150.5, apple.getWeight());
        
        // Verify the second fruit
        Fruit banana = fruits.get(1);
        assertEquals("Banana", banana.getName());
        assertEquals("Yellow", banana.getColor());
        assertEquals(120.0, banana.getWeight());
        
        // Verify the request was made as expected
        verify(getRequestedFor(urlEqualTo(FRUITS_ENDPOINT))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void testGetAllFruitsEmptyResponse() throws IOException {
        // Arrange
        stubFor(get(urlEqualTo(FRUITS_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));

        // Act
        List<Fruit> fruits = fruitService.getAllFruits();

        // Assert
        assertTrue(fruits.isEmpty());
        
        // Verify request
        verify(getRequestedFor(urlEqualTo(FRUITS_ENDPOINT)));
    }

    @Test
    void testGetAllFruitsServerError() {
        // Arrange
        stubFor(get(urlEqualTo(FRUITS_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Internal Server Error")));

        // Act & Assert
        Exception exception = assertThrows(IOException.class, () -> {
            fruitService.getAllFruits();
        });
        
        assertTrue(exception.getMessage().contains("HTTP GET request failed"));
        
        // Verify request
        verify(getRequestedFor(urlEqualTo(FRUITS_ENDPOINT)));
    }

    @Test
    void testFindByNameFound() throws IOException {
        // Arrange
        stubFor(get(urlEqualTo(FRUITS_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"Apple\",\"color\":\"Red\",\"weight\":150.5}," +
                                "{\"name\":\"Banana\",\"color\":\"Yellow\",\"weight\":120.0}]")));

        // Act
        Fruit fruit = fruitService.findByName("Banana");

        // Assert
        assertNotNull(fruit);
        assertEquals("Banana", fruit.getName());
        assertEquals("Yellow", fruit.getColor());
        assertEquals(120.0, fruit.getWeight());
        
        // Verify request
        verify(getRequestedFor(urlEqualTo(FRUITS_ENDPOINT)));
    }

    @Test
    void testFindByNameNotFound() throws IOException {
        // Arrange
        stubFor(get(urlEqualTo(FRUITS_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"Apple\",\"color\":\"Red\",\"weight\":150.5}," +
                                "{\"name\":\"Banana\",\"color\":\"Yellow\",\"weight\":120.0}]")));

        // Act
        Fruit fruit = fruitService.findByName("Orange");

        // Assert
        assertNull(fruit);
        
        // Verify request
        verify(getRequestedFor(urlEqualTo(FRUITS_ENDPOINT)));
    }
} 