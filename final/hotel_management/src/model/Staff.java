package model;

/**
 * Represents a hotel staff member.
 * Inherits from Person — demonstrates inheritance.
 * Demonstrates polymorphism via overridden getRole() and getSummary().
 */
public class Staff extends Person {

    private String department;
    private String position;
    private double salary;

    public Staff(String id, String firstName, String lastName,
                 String phone, String email, String department, String position, double salary) {
        super(id, firstName, lastName, phone, email);
        this.department = department;
        this.position = position;
        this.salary = salary;
    }

    // Getters and setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    /** Overrides abstract method from Person — polymorphism. */
    @Override
    public String getRole() { return "Staff"; }

    /** Overrides getSummary() from Manageable interface. */
    @Override
    public String getSummary() {
        return String.format("STAFF  | ID: %-8s | %-22s | Dept: %-12s | Position: %s",
                getId(), getFullName(), department, position);
    }

    /** Serializes staff to a CSV line for file storage. */
    public String toFileString() {
        return String.join(",", getId(), getFirstName(), getLastName(),
                getPhone(), getEmail(), department, position, String.valueOf(salary));
    }

    /** Deserializes a Staff object from a CSV line. */
    public static Staff fromFileString(String line) {
        String[] p = line.split(",", -1);
        return new Staff(p[0], p[1], p[2], p[3], p[4], p[5], p[6], Double.parseDouble(p[7]));
    }
}
