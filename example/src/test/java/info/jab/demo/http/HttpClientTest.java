package info.jab.demo.http;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HttpClientTest {

    @Mock
    private HttpURLConnection mockConnection;

    private HttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = new HttpClient() {
            @Override
            protected HttpURLConnection openConnection(URL url) throws IOException {
                return mockConnection;
            }
        };
    }

    @Test
    void testGetSuccessful() throws IOException {
        // Setup mock connection
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        ByteArrayInputStream responseStream = new ByteArrayInputStream(
                "Test Response".getBytes(StandardCharsets.UTF_8));
        when(mockConnection.getInputStream()).thenReturn(responseStream);
        
        // Execute test
        String result = httpClient.get("http://test.com/api");
        
        // Verify
        assertEquals("Test Response", result);
        verify(mockConnection).disconnect();
    }
    
    @Test
    void testGetFailureResponseCode() throws IOException {
        // Setup mock connection
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
        
        // Execute test and verify exception
        IOException exception = assertThrows(IOException.class, () -> {
            httpClient.get("http://test.com/api");
        });
        
        // Verify
        assertTrue(exception.getMessage().contains("HTTP GET request failed"));
        verify(mockConnection).disconnect();
    }
} 