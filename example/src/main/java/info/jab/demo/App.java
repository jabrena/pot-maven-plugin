package info.jab.demo;

import info.jab.demo.http.HttpClient;
import info.jab.demo.model.Fruit;
import info.jab.demo.service.FruitService;

import java.io.IOException;
import java.util.List;

/**
 * Main application class for the Fruit Demo.
 */
public class App {
    // Default service URL (can be overridden through command line args)
    private static final String DEFAULT_SERVICE_URL = "https://example.com/api/fruits";
    
    public static void main(String[] args) {
        try {
            String serviceUrl = args.length > 0 ? args[0] : DEFAULT_SERVICE_URL;
            System.out.println("Fetching fruits from: " + serviceUrl);
            
            // Create dependencies
            HttpClient httpClient = new HttpClient();
            FruitService fruitService = new FruitService(httpClient, serviceUrl);
            
            // Run application
            App app = new App();
            app.run(fruitService);
        } catch (IOException e) {
            System.err.println("Error fetching fruit data: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        }
    }
    
    /**
     * Runs the application with the provided fruit service.
     *
     * @param fruitService the fruit service to use
     * @throws IOException if an error occurs while fetching data
     */
    public void run(FruitService fruitService) throws IOException {
        List<Fruit> fruits = fruitService.getAllFruits();
        
        System.out.println("Found " + fruits.size() + " fruits:");
        for (Fruit fruit : fruits) {
            System.out.println(" - " + fruit.getName() + " (" + fruit.getColor() + ", " + fruit.getWeight() + "g)");
        }
        
        // Example of finding a specific fruit
        Fruit apple = fruitService.findByName("apple");
        if (apple != null) {
            System.out.println("\nFound apple: " + apple);
        } else {
            System.out.println("\nNo apple found in the list.");
        }
    }
}
