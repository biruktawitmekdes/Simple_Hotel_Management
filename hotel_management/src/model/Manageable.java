package model;

/**
 * Interface defining standard CRUD operations for manageable entities.
 * Demonstrates interface implementation as required by OOP guidelines.
 */
public interface Manageable {
    /**
     * Returns a unique identifier string for the entity.
     */
    String getId();

    /**
     * Returns a formatted summary string for display purposes.
     */
    String getSummary();

    /**
     * Validates the entity's data fields.
     * @return true if all required fields are valid, false otherwise.
     */
    boolean validate();
}
