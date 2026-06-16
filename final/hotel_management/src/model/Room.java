package model;

/**
 * Represents a physical hotel room.
 * 
 * The Room class encapsulates all information about a specific room in the hotel,
 * including its location, type, current availability status, and amenities.
 * Rooms can be booked by guests, and their status is managed by the HotelService.
 * 
 * INHERITANCE: Implements Manageable interface
 * 
 * KEY CONCEPTS:
 * - Each room has a unique number (primary identifier)
 * - Room type determines base price (SINGLE, DOUBLE, SUITE, DELUXE, PRESIDENTIAL)
 * - Status tracks availability (AVAILABLE, OCCUPIED, RESERVED, UNDER_MAINTENANCE)
 * - Amenities (WiFi, AC) are tracked as boolean flags
 * 
 * @author Hotel Management System
 * @version 1.0
 */
public class Room implements Manageable {

    /**
     * Unique room number/identifier (e.g., "101", "202", "305").
     * Format: FloorNumber followed by room position (e.g., 101 = Floor 1, Room 1)
     * Used as primary key in the repository.
     */
    private String roomNumber;
    
    /**
     * Type of room (SINGLE, DOUBLE, SUITE, DELUXE, PRESIDENTIAL).
     * Determines the base nightly rate and typical occupancy.
     */
    private RoomType type;
    
    /**
     * Current availability status of the room (AVAILABLE, OCCUPIED, RESERVED, UNDER_MAINTENANCE).
     * Initially set to AVAILABLE when room is created.
     * Changed by booking operations and check-in/check-out.
     */
    private RoomStatus status;
    
    /**
     * Floor number where this room is located.
     * Used for room location and organization.
     * Typically 1-based (1st floor, 2nd floor, etc.)
     */
    private int floor;
    
    /**
     * Whether this room has WiFi connectivity.
     * true = WiFi available, false = No WiFi
     * Used in amenities display and potential pricing adjustments.
     */
    private boolean hasWifi;
    
    /**
     * Whether this room has air conditioning.
     * true = AC available, false = No AC
     * Used in amenities display and comfort features.
     */
    private boolean hasAC;

    /**
     * Constructs a Room with all essential information.
     * 
     * @param roomNumber Unique room identifier (e.g., "101", "202")
     * @param type Room type (SINGLE, DOUBLE, SUITE, DELUXE, PRESIDENTIAL)
     * @param floor Floor number where room is located (e.g., 1, 2, 3)
     * @param hasWifi Whether room has WiFi connectivity
     * @param hasAC Whether room has air conditioning
     * 
     * NOTE: Status is automatically initialized to AVAILABLE when room is created
     */
    public Room(String roomNumber, RoomType type, int floor, boolean hasWifi, boolean hasAC) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.floor = floor;
        this.hasWifi = hasWifi;
        this.hasAC = hasAC;
        this.status = RoomStatus.AVAILABLE;
    }

    // ═════════════════════ ACCESSORS (GETTERS & SETTERS) ═════════════════════
    
    /**
     * Gets the unique room identifier (Manageable interface implementation).
     * @return The room number
     */
    public String getId() { return roomNumber; }

    /**
     * Gets the room number.
     * @return The room's unique identifier
     */
    public String getRoomNumber() { return roomNumber; }

    /**
     * Gets the room type.
     * @return RoomType (SINGLE, DOUBLE, SUITE, DELUXE, PRESIDENTIAL)
     */
    public RoomType getType() { return type; }
    
    /**
     * Sets the room type.
     * @param type The new room type
     */
    public void setType(RoomType type) { this.type = type; }

    /**
     * Gets the current room status.
     * @return RoomStatus (AVAILABLE, OCCUPIED, RESERVED, UNDER_MAINTENANCE)
     */
    public RoomStatus getStatus() { return status; }
    
    /**
     * Sets the room status (AVAILABLE, OCCUPIED, RESERVED, or UNDER_MAINTENANCE).
     * This is typically called by HotelService when booking, check-in, check-out, or maintenance operations occur.
     * @param status The new room status
     */
    public void setStatus(RoomStatus status) { this.status = status; }

    /**
     * Gets the floor number.
     * @return Floor number (typically 1-based: 1, 2, 3, etc.)
     */
    public int getFloor() { return floor; }
    
    /**
     * Sets the floor number.
     * @param floor The new floor number
     */
    public void setFloor(int floor) { this.floor = floor; }

    /**
     * Checks if room has WiFi connectivity.
     * @return true if WiFi available, false otherwise
     */
    public boolean hasWifi() { return hasWifi; }
    
    /**
     * Sets WiFi availability for this room.
     * @param hasWifi true to enable WiFi, false to disable
     */
    public void setHasWifi(boolean hasWifi) { this.hasWifi = hasWifi; }

    /**
     * Checks if room has air conditioning.
     * @return true if AC available, false otherwise
     */
    public boolean hasAC() { return hasAC; }
    
    /**
     * Sets air conditioning availability for this room.
     * @param hasAC true to enable AC, false to disable
     */
    public void setHasAC(boolean hasAC) { this.hasAC = hasAC; }

    /**
     * Gets the nightly price for this room based on its type.
     * @return Base price per night in currency (e.g., 50.0 for $50)
     */
    public double getPricePerNight() { return type.getBasePrice(); }

    /**
     * Checks if this room is available for booking.
     * Room is available only if its status is AVAILABLE.
     * @return true if room can be booked, false if occupied, reserved, or under maintenance
     */
    public boolean isAvailable() { return status == RoomStatus.AVAILABLE; }

    /**
     * Gets a formatted summary of the room for display purposes (Manageable interface).
     * 
     * Format: "ROOM | No: roomNumber | Type: roomType | Floor: floorNum | Status: roomStatus | $/night | WiFi: | AC: "
     * Example: "ROOM   | No: 102    | Type: Double         | Floor: 1 | Status: Available         | $80.00/night | WiFi: Yes | AC: Yes"
     * 
     * @return Columnar-formatted string with room details
     */
    @Override
    public String getSummary() {
        return String.format("ROOM   | No: %-6s | Type: %-14s | Floor: %d | Status: %-18s | $%.2f/night | WiFi: %s | AC: %s",
                roomNumber, type.getDisplayName(), floor, status.getDisplayName(),
                getPricePerNight(), hasWifi ? "Yes" : "No", hasAC ? "Yes" : "No");
    }

    /**
     * Validates that the room has all required fields.
     * 
     * Validation Rules:
     * - roomNumber must be non-null and non-blank
     * - type must be non-null
     * - floor must be >= 0
     * 
     * @return true if all required room fields are valid, false otherwise
     */
    @Override
    public boolean validate() {
        return roomNumber != null && !roomNumber.isBlank() && type != null && floor >= 0;
    }

    /**
     * Serializes room to a CSV line for file storage.
     * Format: roomNumber,type,status,floor,hasWifi,hasAC
     * Example: "101,SINGLE,AVAILABLE,1,true,true"
     * 
     * @return CSV string representing this room
     */
    public String toFileString() {
        return String.join(",", roomNumber, type.name(), status.name(),
                String.valueOf(floor), String.valueOf(hasWifi), String.valueOf(hasAC));
    }

    /**
     * Deserializes a Room object from a CSV line.
     * Parses format: roomNumber,type,status,floor,hasWifi,hasAC
     * 
     * @param line CSV string to deserialize
     * @return A new Room object with data from the CSV line
     */
    public static Room fromFileString(String line) {
        String[] p = line.split(",", -1);
        Room r = new Room(p[0], RoomType.valueOf(p[1]), Integer.parseInt(p[3]),
                Boolean.parseBoolean(p[4]), Boolean.parseBoolean(p[5]));
        r.status = RoomStatus.valueOf(p[2]);
        return r;
    }

    /**
     * Returns string representation of the room (calls getSummary()).
     * @return Room summary string
     */
    @Override
    public String toString() {
        return getSummary();
    }
}
