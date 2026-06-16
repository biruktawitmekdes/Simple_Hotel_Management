package model;


public abstract class Person implements Manageable {

    // Private fields — encapsulation
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    
    public Person(String id, String firstName, String lastName, String phone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

   
    @Override
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return firstName + " " + lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public boolean validate() {
        return id != null && !id.isBlank()
            && firstName != null && !firstName.isBlank()
            && lastName != null && !lastName.isBlank()
            && phone != null && !phone.isBlank();
    }

   
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("[%s] %s | Phone: %s | Email: %s", id, getFullName(), phone, email);
    }
}
