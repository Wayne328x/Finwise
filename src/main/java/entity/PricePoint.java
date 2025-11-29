package entity;

import java.time.LocalDate;

/**
 * Represents the price of a stock at a specific date.
 */
public class PricePoint {

    private final LocalDate date;
    private final double price; // closing price or latest price for that day

    /**
     * @param date  the date of this price (must not be null)
     * @param price the price value (must be >= 0)
     */
    public PricePoint(LocalDate date, double price) {
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must not be negative.");
        }
        this.date = date;
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }
}
