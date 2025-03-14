package info.jab.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FruitTest {

    @Test
    void testFruitCreation() {
        Fruit fruit = new Fruit("Apple", "Red", 150.5);
        
        assertEquals("Apple", fruit.getName());
        assertEquals("Red", fruit.getColor());
        assertEquals(150.5, fruit.getWeight());
    }
    
    @Test
    void testFruitEquality() {
        Fruit fruit1 = new Fruit("Banana", "Yellow", 120.0);
        Fruit fruit2 = new Fruit("Banana", "Yellow", 120.0);
        Fruit fruit3 = new Fruit("Orange", "Orange", 150.0);
        
        assertEquals(fruit1, fruit2);
        assertNotEquals(fruit1, fruit3);
        assertNotEquals(fruit1, null);
        assertNotEquals(fruit1, "Not a fruit");
    }
    
    @Test
    void testHashCode() {
        Fruit fruit1 = new Fruit("Strawberry", "Red", 20.5);
        Fruit fruit2 = new Fruit("Strawberry", "Red", 20.5);
        
        assertEquals(fruit1.hashCode(), fruit2.hashCode());
    }
    
    @Test
    void testToString() {
        Fruit fruit = new Fruit("Grape", "Purple", 5.0);
        String expected = "Fruit{name='Grape', color='Purple', weight=5.0}";
        
        assertEquals(expected, fruit.toString());
    }
} 