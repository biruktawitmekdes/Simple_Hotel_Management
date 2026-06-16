package model;

/**
 * Represents a hotel room.
 * Uses RoomType and RoomStatus enums, and implements Manageable interface.
 * Demonstrates encapsulation with private fields and getters/setters.
 */
public class Room implements Manageable {

    private String roomNumber;
    private RoomType type;
    private RoomStatus status;
    private int floor;
    private boolean hasWifi;
    private boolean hasAC;

    public Room(String roomNumber, RoomType type, int floor, boolean hasWifi, boolean hasAC) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.floor = floor;
        this.hasWifi = hasWifi;
        this.hasAC = hasAC;
        this.status = RoomStatus.AVAILABLE;
    }

    // Getters and setters
    @Override
    public String getId() { return roomNumber; }

    public String getRoomNumber() { return roomNumber; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public boolean hasWifi() { return hasWifi; }
    public void setHasWifi(boolean hasWifi) { this.hasWifi = hasWifi; }

    public boolean hasAC() { return hasAC; }
    public void setHasAC(boolean hasAC) { this.hasAC = hasAC; }

    public double getPricePerNight() { return type.getBasePrice(); }

    public boolean isAvailable() { return status == RoomStatus.AVAILABLE; }

    @Override
    public String getSummary() {
        return String.format("ROOM   | No: %-6s | Type: %-14s | Floor: %d | Status: %-18s | $%.2f/night | WiFi: %s | AC: %s",
                roomNumber, type.getDisplayName(), floor, status.getDisplayName(),
                getPricePerNight(), hasWifi ? "Yes" : "No", hasAC ? "Yes" : "No");
    }

    @Override
    public boolean validate() {
        return roomNumber != null && !roomNumber.isBlank() && type != null && floor >= 0;
    }

    /** Serializes room to CSV line for file storage. */
    public String toFileString() {
        return String.join(",", roomNumber, type.name(), status.name(),
                String.valueOf(floor), String.valueOf(hasWifi), String.valueOf(hasAC));
    }

    /** Deserializes a Room from a CSV line. */
    public static Room fromFileString(String line) {
        String[] p = line.split(",", -1);
        Room r = new Room(p[0], RoomType.valueOf(p[1]), Integer.parseInt(p[3]),
                Boolean.parseBoolean(p[4]), Boolean.parseBoolean(p[5]));
        r.status = RoomStatus.valueOf(p[2]);
        return r;
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
