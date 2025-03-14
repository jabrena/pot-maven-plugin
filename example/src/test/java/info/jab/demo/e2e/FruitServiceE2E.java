package info.jab.demo.e2e;

import info.jab.demo.App;
import info.jab.demo.http.HttpClient;
import info.jab.demo.service.FruitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.github.tomakehurst.wiremock.WireMockServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * End-to-end tests for the complete application.
 * These tests simulate the full flow from the main application through all components.
 */
@Tag("e2e")
class FruitServiceE2E {

    private static final int PORT = 8090;
    private static final String HOST = "localhost";
    private static final String FRUITS_ENDPOINT = "/api/fruits";
    private WireMockServer wireMockServer;
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        // Setup WireMock server
        wireMockServer = new WireMockServer(wireMockConfig().port(PORT));
        wireMockServer.start();
        configureFor(HOST, PORT);
        
        // Capture stdout and stderr
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        // Restore stdout and stderr
        System.setOut(originalOut);
        System.setErr(originalErr);
        
        // Stop WireMock server
        wireMockServer.stop();
    }

    @Test
    void testFullApplicationFlowSuccess() throws Exception {
        // Arrange - Configure mock API response
        stubFor(get(urlEqualTo(FRUITS_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\":\"Apple\",\"color\":\"Red\",\"weight\":150.5}," +
                                "{\"name\":\"Banana\",\"color\":\"Yellow\",\"weight\":120.0}," +
                                "{\"name\":\"Orange\",\"color\":\"Orange\",\"weight\":170.0}]")));
        
        // Create the service URL
        String serviceUrl = "http://" + HOST + ":" + PORT + FRUITS_ENDPOINT;
        
        // Run the application directly as it would be run from main()
        HttpClient httpClient = new HttpClient();
        FruitService fruitService = new FruitService(httpClient, serviceUrl);
        App app = new App();
        app.run(fruitService);
        
        // Get output
        String output = outputStream.toString();
        
        // Assert that output contains expected data
        assertTrue(output.contains("Found 3 fruits:"));
        assertTrue(output.contains("Apple (Red, 150.5g)"));
        assertTrue(output.contains("Banana (Yellow, 120.0g)"));
        assertTrue(output.contains("Orange (Orange, 170.0g)"));
        assertTrue(output.contains("Found apple:"));
        
        // Verify that the HTTP request was made as expected
        verify(getRequestedFor(urlEqualTo(FRUITS_ENDPOINT))
                .withHeader("Accept", equalTo("application/json")));
    }

    @Test
    void testFullApplicationFlowWithNetworkError() {
        // Arrange - Configure mock API to return an error
        stubFor(get(urlEqualTo(FRUITS_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(503)
                        .withHeader("Content-Type", "text/plain")
                        .withBody("Service Unavailable")));
        
        // Create the service URL
        String serviceUrl = "http://" + HOST + ":" + PORT + FRUITS_ENDPOINT;
        
        // Run the application directly - should throw an exception
        HttpClient httpClient = new HttpClient();
        FruitService fruitService = new FruitService(httpClient, serviceUrl);
        App app = new App();
        
        Exception exception = assertThrows(IOException.class, () -> {
            app.run(fruitService);
        });
        
        // Assert that the exception message is as expected
        assertTrue(exception.getMessage().contains("HTTP GET request failed"));
        
        // Verify that the HTTP request was made as expected
        verify(getRequestedFor(urlEqualTo(FRUITS_ENDPOINT)));
    }

    @Test
    void testFullApplicationFlowWithEmptyResponse() throws Exception {
        // Arrange - Configure mock API to return empty array
        stubFor(get(urlEqualTo(FRUITS_ENDPOINT))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[]")));
        
        // Create the service URL
        String serviceUrl = "http://" + HOST + ":" + PORT + FRUITS_ENDPOINT;
        
        // Run the application
        HttpClient httpClient = new HttpClient();
        FruitService fruitService = new FruitService(httpClient, serviceUrl);
        App app = new App();
        app.run(fruitService);
        
        // Get output
        String output = outputStream.toString();
        
        // Assert that output contains expected data
        assertTrue(output.contains("Found 0 fruits:"));
        assertTrue(output.contains("No apple found"));
        
        // Verify that the HTTP request was made as expected
        verify(getRequestedFor(urlEqualTo(FRUITS_ENDPOINT)));
    }
} 