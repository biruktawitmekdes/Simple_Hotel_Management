package model;

/**
 * Abstract base class representing a person (either Guest or Staff) in the hotel system.
 * 
 * This abstract class encapsulates common attributes and behaviors of all persons in the system.
 * It serves as the parent class for Guest and Staff, demonstrating inheritance and abstraction.
 * All person-related data is carefully encapsulated using private fields with public accessors.
 * 
 * DESIGN PATTERNS: Template Method, Inheritance Hierarchy
 * OOP PRINCIPLES:
 * - Abstraction: Common attributes and methods are abstracted into a base class
 * - Encapsulation: All fields are private with controlled getter/setter access
 * - Polymorphism: Subclasses override abstract method getRole() for different implementations
 * - Inheritance: Guest and Staff inherit common person attributes and behavior
 * 
 * SUBCLASSES: Guest (adds nationality, idNumber, totalStays), Staff (adds department, position, salary)
 * 
 * @author Hotel Management System
 * @version 1.0
 */
public abstract class Person implements Manageable {

    // ═════════════════════ FIELDS (ENCAPSULATED) ═════════════════════
    
    /**
     * Unique identifier for this person in the system.
     * Format: Type prefix + number (e.g., \"G001\" for guests, \"S001\" for staff)
     * Used as primary key in repositories and for lookups.
     */
    private String id;
    
    /**
     * Person's first/given name.
     * Required field - cannot be null or blank per validation rules.
     */
    private String firstName;
    
    /**
     * Person's family/last name.
     * Required field - cannot be null or blank per validation rules.
     */
    private String lastName;
    
    /**
     * Person's contact phone number.
     * Required field - cannot be null or blank per validation rules.
     * Format may vary (no strict validation on format, only presence).
     */
    private String phone;
    
    /**
     * Person's contact email address.
     * Required field - cannot be null or blank per validation rules.
     * Format validation is minimal (only presence checked in base class).
     */
    private String email;

    // ═════════════════════ CONSTRUCTOR ═════════════════════
    
    /**
     * Constructs a Person with all essential contact information.
     * 
     * This constructor initializes all required fields for a person.
     * No field validation is performed here; validation occurs in the validate() method.
     * Subclasses must call super() to properly initialize these base fields.
     * 
     * @param id Unique identifier (required, non-empty) - Examples: \"G001\", \"S001\"
     * @param firstName Person's first name (required, non-empty)
     * @param lastName Person's last name (required, non-empty)
     * @param phone Contact phone number (required, non-empty) - Examples: \"555-1234\", \"+1-555-1234\"
     * @param email Contact email address (required, non-empty) - Examples: \"john@example.com\"
     */
    public Person(String id, String firstName, String lastName, String phone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    // ═════════════════════ GETTERS & SETTERS (ACCESSORS) ═════════════════════
    
    /**
     * Gets the unique identifier for this person (Manageable interface implementation).
     * @return The person's unique ID string (e.g., \"G001\", \"S001\")
     */
    @Override
    public String getId() { return id; }
    
    /**
     * Sets the unique identifier for this person.
     * WARNING: Changing ID after creation may break repository lookups and data integrity.
     * @param id The new unique identifier
     */
    public void setId(String id) { this.id = id; }

    /**
     * Gets the person's first/given name.
     * @return The first name
     */
    public String getFirstName() { return firstName; }
    
    /**
     * Sets the person's first/given name.
     * @param firstName The new first name (should not be null or blank for valid person)
     */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /**
     * Gets the person's family/last name.
     * @return The last name
     */
    public String getLastName() { return lastName; }
    
    /**
     * Sets the person's family/last name.
     * @param lastName The new last name (should not be null or blank for valid person)
     */
    public void setLastName(String lastName) { this.lastName = lastName; }

    /**
     * Gets the person's complete name (first + last).
     * Concatenates firstName and lastName with a space separator.
     * @return Full name in format \"FirstName LastName\" - Example: \"John Doe\"
     */
    public String getFullName() { return firstName + " " + lastName; }

    /**
     * Gets the person's contact phone number.
     * @return The phone number string
     */
    public String getPhone() { return phone; }
    
    /**
     * Sets the person's contact phone number.
     * @param phone The phone number (should not be null or blank for valid person)
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**
     * Gets the person's contact email address.
     * @return The email address string
     */
    public String getEmail() { return email; }
    
    /**
     * Sets the person's contact email address.
     * @param email The email address (should not be null or blank for valid person)
     */
    public void setEmail(String email) { this.email = email; }

    // ═════════════════════ VALIDATION & INTERFACE METHODS ═════════════════════
    
    /**
     * Validates that all required person fields are present and non-blank.
     * 
     * This method ensures that the person object is in a valid state for persistence.
     * It checks that id, firstName, lastName, and phone are not null and not blank strings.
     * Subclasses typically override this to add additional field-specific validations.
     * 
     * Validation Rules:
     * - id must be non-null and non-blank
     * - firstName must be non-null and non-blank
     * - lastName must be non-null and non-blank  
     * - phone must be non-null and non-blank
     * - email validation is handled in subclasses if needed
     * 
     * @return true if all base person fields are valid, false if any validation check fails
     * @see Guest#validate() for guest-specific validation (adds idNumber check)
     */
    @Override
    public boolean validate() {
        return id != null && !id.isBlank()
            && firstName != null && !firstName.isBlank()
            && lastName != null && !lastName.isBlank()
            && phone != null && !phone.isBlank();
    }

    /**
     * Abstract method that returns this person's role/type in the system.
     * 
     * This method demonstrates the Template Method pattern and polymorphism.
     * Each subclass implements this to return their specific role type.
     * Used primarily for display, filtering, and permission checking.
     * 
     * POLYMORPHISM: Subclasses provide their own implementation:
     * - Guest.getRole() returns "Guest"
     * - Staff.getRole() returns "Staff"
     * 
     * @return A string representing the person's role type
     */
    public abstract String getRole();

    /**
     * Provides a string representation of the person for debugging and display.
     * 
     * Format: [ID] FirstName LastName | Phone: phone | Email: email
     * Example: [G001] John Doe | Phone: 555-1234 | Email: john@example.com
     * 
     * This is primarily used for logging and debugging, not for formal display.
     * For formal display in lists, use getSummary() from the Manageable interface instead,
     * which provides a more structured columnar format.
     * 
     * @return A formatted string containing the person's ID, full name, phone, and email
     */
    @Override
    public String toString() {
        return String.format("[%s] %s | Phone: %s | Email: %s", id, getFullName(), phone, email);
    }
}
