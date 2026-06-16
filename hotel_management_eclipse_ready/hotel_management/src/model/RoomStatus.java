package model;

/**
 * Enumeration for room availability status.
 */
public enum RoomStatus {
    AVAILABLE("Available"),
    OCCUPIED("Occupied"),
    UNDER_MAINTENANCE("Under Maintenance"),
    RESERVED("Reserved");

    private final String displayName;

    RoomStatus(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }

    @Override
    public String toString() { return displayName; }
}
