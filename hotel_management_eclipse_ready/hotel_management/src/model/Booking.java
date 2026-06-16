package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a hotel booking/reservation.
 * Associates a Guest with a Room for a date range.
 * Demonstrates object association and encapsulation.
 */
public class Booking implements Manageable {

    private String bookingId;
    private String guestId;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean isPaid;
    private String notes;

    public Booking(String bookingId, String guestId, String roomNumber,
                   LocalDate checkIn, LocalDate checkOut) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.isPaid = false;
        this.notes = "";
    }

    // Getters and setters
    @Override
    public String getId() { return bookingId; }

    public String getBookingId() { return bookingId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { this.isPaid = paid; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    /** Calculates number of nights. */
    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    /** Calculates total cost given the price per night. */
    public double calculateTotal(double pricePerNight) {
        return getNights() * pricePerNight;
    }

    public boolean qualifiesForLoyaltyDiscount() {
        return getNights() >= 3;
    }

    public double calculateDiscount(double pricePerNight) {
        return qualifiesForLoyaltyDiscount() ? calculateTotal(pricePerNight) * 0.15 : 0;
    }

    public double calculateTotalWithDiscount(double pricePerNight) {
        return calculateTotal(pricePerNight) - calculateDiscount(pricePerNight);
    }

    @Override
    public String getSummary() {
        return String.format(
                "BOOKING| ID: %-8s | Guest: %-8s | Room: %-6s | CheckIn: %s | CheckOut: %s | Nights: %d | Paid: %s",
                bookingId, guestId, roomNumber, checkIn, checkOut, getNights(), isPaid ? "Yes" : "No");
    }

    @Override
    public boolean validate() {
        return bookingId != null && !bookingId.isBlank()
            && guestId != null && !guestId.isBlank()
            && roomNumber != null && !roomNumber.isBlank()
            && checkIn != null && checkOut != null
            && checkOut.isAfter(checkIn);
    }

    /** Serializes booking to CSV line for file storage. */
    public String toFileString() {
        return String.join(",", bookingId, guestId, roomNumber,
                checkIn.toString(), checkOut.toString(),
                String.valueOf(isPaid), notes.replace(",", ";"));
    }

    /** Deserializes a Booking from a CSV line. */
    public static Booking fromFileString(String line) {
        String[] p = line.split(",", -1);
        Booking b = new Booking(p[0], p[1], p[2],
                LocalDate.parse(p[3]), LocalDate.parse(p[4]));
        b.isPaid = Boolean.parseBoolean(p[5]);
        b.notes = p.length > 6 ? p[6].replace(";", ",") : "";
        return b;
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
