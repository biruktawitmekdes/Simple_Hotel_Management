package model;

/**
 * Enumeration for room availability status/state.
 * 
 * RoomStatus defines all possible states a room can be in at any given time.
 * These states are managed by the HotelService and change based on booking,
 * check-in, check-out, and maintenance operations.
 * 
 * STATE TRANSITIONS:
 * AVAILABLE → OCCUPIED (guest checks in)
 * OCCUPIED → AVAILABLE (guest checks out)
 * AVAILABLE → RESERVED (booking created)
 * RESERVED → OCCUPIED (booked guest checks in)
 * Any state → UNDER_MAINTENANCE (maintenance needed)
 * UNDER_MAINTENANCE → AVAILABLE (maintenance complete)
 * 
 * @author Hotel Management System
 * @version 1.0
 */
public enum RoomStatus {
    
    /**
     * Room is ready and available for booking.
     * Display Name: "Available"
     * Can transition to: OCCUPIED, RESERVED, or UNDER_MAINTENANCE
     */
    AVAILABLE("Available"),
    
    /**
     * Room is currently occupied by a guest.
     * Display Name: "Occupied"
     * Can transition to: AVAILABLE (on checkout) or UNDER_MAINTENANCE
     */
    OCCUPIED("Occupied"),
    
    /**
     * Room is under maintenance and unavailable for booking.
     * Display Name: "Under Maintenance"
     * Can transition to: AVAILABLE (when maintenance is complete)
     */
    UNDER_MAINTENANCE("Under Maintenance"),
    
    /**
     * Room is reserved for an upcoming booking.
     * Display Name: "Reserved"
     * Can transition to: OCCUPIED (on check-in), AVAILABLE (if booking cancelled), or UNDER_MAINTENANCE
     */
    RESERVED("Reserved");

    /**
     * Human-readable display name for this room status.
     * Used in UI display, reports, and room queries.
     */
    private final String displayName;

    /**
     * Constructs a RoomStatus enum constant with display name.
     * 
     * @param displayName Human-readable name for UI display (e.g., "Available", "Occupied")
     */
    RoomStatus(String displayName) { this.displayName = displayName; }

    /**
     * Gets the display name for this room status.
     * 
     * @return Human-readable status name suitable for UI display
     *         Examples: "Available", "Occupied", "Under Maintenance", "Reserved"
     */
    public String getDisplayName() { return displayName; }

    /**
     * Returns the display name string representation.
     * 
     * @return Display name (same as getDisplayName())
     */
    @Override
    public String toString() { return displayName; }
}
