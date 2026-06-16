package model;

/**
 * Enumeration for different types of hotel rooms with pricing information.
 * 
 * RoomType defines all available room categories in the hotel, each with a display name
 * and base nightly rate. This enum demonstrates the use of fixed value sets in Java,
 * ensuring type safety and preventing invalid room type assignments.
 * 
 * DESIGN PATTERN: Type-Safe Enum Pattern
 * USAGE: Each Room object has exactly one RoomType, determining its base price
 * 
 * @author Hotel Management System
 * @version 1.0
 */
public enum RoomType {
    
    /**
     * Single bed room.
     * Display Name: "Single"
     * Base Price: $50.00 per night
     * Typical capacity: 1 guest
     */
    SINGLE("Single", 50.0),
    
    /**
     * Double bed room.
     * Display Name: "Double"
     * Base Price: $80.00 per night
     * Typical capacity: 2 guests
     */
    DOUBLE("Double", 80.0),
    
    /**
     * Suite room with separate living area.
     * Display Name: "Suite"
     * Base Price: $150.00 per night
     * Typical capacity: 2-4 guests
     */
    SUITE("Suite", 150.0),
    
    /**
     * Deluxe room with premium amenities.
     * Display Name: "Deluxe"
     * Base Price: $200.00 per night
     * Typical capacity: 2 guests
     */
    DELUXE("Deluxe", 200.0),
    
    /**
     * Presidential/Luxury suite with top-tier amenities.
     * Display Name: "Presidential"
     * Base Price: $500.00 per night
     * Typical capacity: 2-4 guests
     */
    PRESIDENTIAL("Presidential", 500.0);

    /**
     * Human-readable display name for this room type.
     * Used in UI display, reports, and booking confirmations.
     */
    private final String displayName;
    
    /**
     * Base nightly rate for this room type in currency units.
     * This is the standard price per night before any discounts or extra charges.
     * Actual booking price may be modified by loyalty discounts or dynamic pricing.
     */
    private final double basePrice;

    /**
     * Constructs a RoomType enum constant with display name and base price.
     * 
     * @param displayName Human-readable name for UI display (e.g., "Single", "Double")
     * @param basePrice Base nightly rate in currency (e.g., 50.0 for $50/night)
     */
    RoomType(String displayName, double basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    /**
     * Gets the display name for this room type.
     * 
     * @return Human-readable room type name suitable for UI display
     *         Examples: "Single", "Double", "Suite", "Deluxe", "Presidential"
     */
    public String getDisplayName() { return displayName; }
    
    /**
     * Gets the base nightly rate for this room type.
     * 
     * @return Base price in currency per night (e.g., 50.0 for $50.00)
     *         This price is used as the basis for booking calculations
     */
    public double getBasePrice() { return basePrice; }

    /**
     * Returns the display name string representation.
     * 
     * @return Display name (same as getDisplayName())
     */
    @Override
    public String toString() { return displayName; }
}
