package info.jab.demo.model;

import java.util.Objects;

/**
 * Represents a fruit with its properties.
 */
public class Fruit {
    private final String name;
    private final String color;
    private final double weight;
    
    /**
     * Creates a new Fruit instance.
     *
     * @param name   the name of the fruit
     * @param color  the color of the fruit
     * @param weight the weight of the fruit in grams
     */
    public Fruit(String name, String color, double weight) {
        this.name = name;
        this.color = color;
        this.weight = weight;
    }
    
    /**
     * Returns the name of the fruit.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the color of the fruit.
     *
     * @return the color
     */
    public String getColor() {
        return color;
    }
    
    /**
     * Returns the weight of the fruit in grams.
     *
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fruit fruit = (Fruit) o;
        return Double.compare(fruit.weight, weight) == 0 &&
               Objects.equals(name, fruit.name) &&
               Objects.equals(color, fruit.color);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, color, weight);
    }
    
    @Override
    public String toString() {
        return "Fruit{" +
               "name='" + name + '\'' +
               ", color='" + color + '\'' +
               ", weight=" + weight +
               '}';
    }
} 