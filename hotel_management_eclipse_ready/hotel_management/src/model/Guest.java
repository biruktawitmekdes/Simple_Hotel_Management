package model;

/**
 * Represents a hotel guest.
 * Inherits from Person and adds guest-specific fields.
 * Demonstrates inheritance and method overriding (polymorphism).
 */
public class Guest extends Person {

    private String nationality;
    private String idNumber;        // National ID or Passport number
    private int totalStays;         // Number of previous stays

    public Guest(String id, String firstName, String lastName,
                 String phone, String email, String nationality, String idNumber) {
        super(id, firstName, lastName, phone, email);
        this.nationality = nationality;
        this.idNumber = idNumber;
        this.totalStays = 0;
    }

    // Getters and setters
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public int getTotalStays() { return totalStays; }
    public void incrementStays() { this.totalStays++; }

    /** Overrides abstract method from Person — polymorphism. */
    @Override
    public String getRole() { return "Guest"; }

    /** Overrides getSummary() from Manageable interface. */
    @Override
    public String getSummary() {
        return String.format("GUEST  | ID: %-8s | %-22s | Phone: %-15s | Nationality: %s",
                getId(), getFullName(), getPhone(), nationality);
    }

    @Override
    public boolean validate() {
        return super.validate() && idNumber != null && !idNumber.isBlank();
    }

    /** Serializes guest to a CSV line for file storage. */
    public String toFileString() {
        return String.join(",", getId(), getFirstName(), getLastName(),
                getPhone(), getEmail(), nationality, idNumber, String.valueOf(totalStays));
    }

    /** Deserializes a Guest from a CSV line. */
    public static Guest fromFileString(String line) {
        String[] p = line.split(",", -1);
        Guest g = new Guest(p[0], p[1], p[2], p[3], p[4], p[5], p[6]);
        g.totalStays = Integer.parseInt(p[7]);
        return g;
    }
}
