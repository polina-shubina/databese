package org.example;
import java.io.Serializable;
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String author;
    private final double price;
    private final boolean availability;

    public Book(int id, String name, String author, double price, boolean availability) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.price = price;
        this.availability = availability;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailability() {
        return availability;
    }

    @Override
    public String toString() {
        return id + "    " + name + "    " + author + "    " + price + "    " + "    " + availability;
    }
}