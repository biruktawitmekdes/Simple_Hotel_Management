package model;

/**
 * Enumeration for different types of hotel rooms.
 * Demonstrates use of enum for fixed values.
 */
public enum RoomType {
    SINGLE("Single", 50.0),
    DOUBLE("Double", 80.0),
    SUITE("Suite", 150.0),
    DELUXE("Deluxe", 200.0),
    PRESIDENTIAL("Presidential", 500.0);

    private final String displayName;
    private final double basePrice;

    RoomType(String displayName, double basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String getDisplayName() { return displayName; }
    public double getBasePrice() { return basePrice; }

    @Override
    public String toString() { return displayName; }
}
