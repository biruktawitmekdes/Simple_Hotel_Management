package model;

/**
 * Interface defining standard CRUD operations for manageable entities.
 * 
 * This interface serves as a contract for all entities that can be managed, stored, and validated
 * within the Hotel Management System. It enforces a consistent API for accessing entity information
 * and validating entity state. All major model classes (Guest, Staff, Room, Booking) implement
 * this interface to ensure standardized behavior across the system.
 * 
 * DESIGN PATTERN: Contract/Interface Pattern
 * PRINCIPLES: Single Responsibility Principle - Interface focuses on entity management
 * 
 * IMPLEMENTATIONS: Person (abstract), Guest, Staff, Room, Booking
 * 
 * @author Hotel Management System
 * @version 1.0
 */
public interface Manageable {
    
    /**
     * Retrieves the unique identifier for this entity.
     * 
     * The identifier is typically an alphanumeric string that uniquely identifies
     * this entity within its repository. For guests/staff it's an ID code, for rooms
     * it's the room number, and for bookings it's the booking reference number.
     * 
     * This is used as the primary key in the Repository<T> for storage and lookup.
     * 
     * @return A non-null String representing the entity's unique identifier.
     *         Examples: "G001" for guests, "102" for rooms, "BK2024001" for bookings
     */
    String getId();

    /**
     * Generates a formatted summary string for display purposes.
     * 
     * This method produces a human-readable representation of the entity in a compact
     * format suitable for console output, reports, or logging. The format is typically
     * columnar, showing the most relevant information for the entity type. This is used
     * extensively in the UI layer for displaying lists and search results.
     * 
     * @return A formatted String containing the entity's key information.
     *         Examples:
     *         - Guest: "GUEST  | ID: G0001    | John Doe               | Phone: 555-1234      | Nationality: Ethiopia"
     *         - Room:  "ROOM   | No: 102    | Type: Double         | Floor: 1 | Status: Available         | $80.00/night | WiFi: Yes | AC: Yes"
     *         - Staff: "STAFF  | ID: S0001    | Jane Doe               | Dept: Front Desk | Position: Manager"
     *         - Booking: "BK001" (minimal - used in lists)
     */
    String getSummary();

    /**
     * Validates all required data fields for this entity.
     * 
     * This method performs data validation to ensure the entity is in a valid state
     * before being persisted or used in business operations. Each implementation defines
     * its own validation rules based on the specific entity requirements. For example,
     * Person validates that ID, names, phone, and email are not blank; Room validates
     * that room number and type are present.
     * 
     * Validation is called before:
     * - Saving the entity to persistent storage
     * - Creating bookings or making reservations
     * - Performing business logic operations
     * - Processing transactions
     * 
     * @return true if all required fields are valid and non-null, false if any validation fails.
     *         Return true means the entity is ready for persistence and business logic operations.
     */
    boolean validate();
}
