package info.jab.demo.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Simple HTTP client for making web requests.
 */
public class HttpClient {
    
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;
    
    /**
     * Performs a GET request to the specified URL.
     *
     * @param urlString the URL to connect to
     * @return the response body as a String
     * @throws IOException if an I/O error occurs
     */
    public String get(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = openConnection(url);
        
        try {
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP GET request failed with error code: " + responseCode);
            }
            
            return readResponse(connection);
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * Opens a connection to the given URL.
     * Protected for testing - allows override in tests for mocking.
     *
     * @param url the URL to connect to
     * @return the HTTP connection
     * @throws IOException if an I/O error occurs
     */
    protected HttpURLConnection openConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }
    
    /**
     * Reads the HTTP response body.
     *
     * @param connection the active HTTP connection
     * @return the response body as a String
     * @throws IOException if an I/O error occurs
     */
    private String readResponse(HttpURLConnection connection) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            return response.toString();
        }
    }
} 