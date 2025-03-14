package info.jab.demo.service;

import info.jab.demo.http.HttpClient;
import info.jab.demo.model.Fruit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for retrieving fruit data from a remote API.
 */
public class FruitService {
    private static final Pattern FRUIT_PATTERN = 
        Pattern.compile("\\{\\s*\"name\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"color\"\\s*:\\s*\"([^\"]+)\"\\s*,\\s*\"weight\"\\s*:\\s*([\\d.]+)\\s*\\}");
    
    private final HttpClient httpClient;
    private final String serviceUrl;
    
    /**
     * Creates a new FruitService with the specified HTTP client and service URL.
     *
     * @param httpClient the HTTP client to use for requests
     * @param serviceUrl the URL of the fruit service
     */
    public FruitService(HttpClient httpClient, String serviceUrl) {
        this.httpClient = httpClient;
        this.serviceUrl = serviceUrl;
    }
    
    /**
     * Fetches all fruits from the service.
     *
     * @return a list of fruits
     * @throws IOException if a communication error occurs
     * @throws IllegalStateException if the response cannot be parsed
     */
    public List<Fruit> getAllFruits() throws IOException {
        String response = httpClient.get(serviceUrl);
        return parseFruits(response);
    }
    
    /**
     * Parses a JSON response containing fruit data.
     * 
     * Note: This is a simple parser implementation for demonstration purposes.
     * In a real application, you would typically use a JSON library.
     *
     * @param jsonResponse the JSON response to parse
     * @return a list of parsed Fruit objects
     * @throws IllegalStateException if the response cannot be parsed
     */
    List<Fruit> parseFruits(String jsonResponse) {
        List<Fruit> fruits = new ArrayList<>();
        Matcher matcher = FRUIT_PATTERN.matcher(jsonResponse);
        
        while (matcher.find()) {
            String name = matcher.group(1);
            String color = matcher.group(2);
            double weight = Double.parseDouble(matcher.group(3));
            
            fruits.add(new Fruit(name, color, weight));
        }
        
        if (fruits.isEmpty() && !jsonResponse.trim().equals("[]")) {
            throw new IllegalStateException("Failed to parse fruit data from response: " + jsonResponse);
        }
        
        return fruits;
    }
    
    /**
     * Finds a fruit by name.
     *
     * @param name the name of the fruit to find
     * @return the found fruit, or null if not found
     * @throws IOException if a communication error occurs
     */
    public Fruit findByName(String name) throws IOException {
        List<Fruit> fruits = getAllFruits();
        
        return fruits.stream()
                .filter(fruit -> fruit.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
} 