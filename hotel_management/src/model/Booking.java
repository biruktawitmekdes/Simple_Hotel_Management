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
    private double foodCharges;
    private double gymCharges;
    private double spaCharges;
    // Detailed food breakdown
    private double foodTibs;
    private double foodDorowet;
    private double foodBfe;
    private double foodBreakfastMeals;
    private double foodFruits;
    // Detailed beverage breakdown
    private double bevWine;
    private double bevAlcoholic;
    private double bevWater;
    private double bevJuice;
    private double bevNonAlcoholic;

    private int foodTibsQty;
    private int foodDorowetQty;
    private int foodBfeQty;
    private int foodBreakfastMealsQty;
    private int foodFruitsQty;

    private int bevWineQty;
    private int bevAlcoholicQty;
    private int bevWaterQty;
    private int bevJuiceQty;
    private int bevNonAlcoholicQty;

    private int gymSessions;
    private int spaSessions;

    public Booking(String bookingId, String guestId, String roomNumber,
                   LocalDate checkIn, LocalDate checkOut) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.isPaid = false;
        this.notes = "";
        this.foodCharges = 0.0;
        this.gymCharges = 0.0;
        this.spaCharges = 0.0;
        this.foodTibsQty = 0;
        this.foodDorowetQty = 0;
        this.foodBfeQty = 0;
        this.foodBreakfastMealsQty = 0;
        this.foodFruitsQty = 0;
        this.bevWineQty = 0;
        this.bevAlcoholicQty = 0;
        this.bevWaterQty = 0;
        this.bevJuiceQty = 0;
        this.bevNonAlcoholicQty = 0;
        this.gymSessions = 0;
        this.spaSessions = 0;
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

    public double getFoodCharges() { return foodCharges; }
    public void setFoodCharges(double foodCharges) { this.foodCharges = Math.max(0, foodCharges); }

    public double getFoodTibs() { return foodTibs; }
    public void setFoodTibs(double foodTibs) { this.foodTibs = Math.max(0, foodTibs); }

    public double getFoodDorowet() { return foodDorowet; }
    public void setFoodDorowet(double foodDorowet) { this.foodDorowet = Math.max(0, foodDorowet); }

    public double getFoodBfe() { return foodBfe; }
    public void setFoodBfe(double foodBfe) { this.foodBfe = Math.max(0, foodBfe); }

    public double getFoodBreakfastMeals() { return foodBreakfastMeals; }
    public void setFoodBreakfastMeals(double foodBreakfastMeals) { this.foodBreakfastMeals = Math.max(0, foodBreakfastMeals); }

    public double getFoodFruits() { return foodFruits; }
    public void setFoodFruits(double foodFruits) { this.foodFruits = Math.max(0, foodFruits); }

    public int getFoodTibsQty() { return foodTibsQty; }
    public void setFoodTibsQty(int foodTibsQty) { this.foodTibsQty = Math.max(0, foodTibsQty); }

    public int getFoodDorowetQty() { return foodDorowetQty; }
    public void setFoodDorowetQty(int foodDorowetQty) { this.foodDorowetQty = Math.max(0, foodDorowetQty); }

    public int getFoodBfeQty() { return foodBfeQty; }
    public void setFoodBfeQty(int foodBfeQty) { this.foodBfeQty = Math.max(0, foodBfeQty); }

    public int getFoodBreakfastMealsQty() { return foodBreakfastMealsQty; }
    public void setFoodBreakfastMealsQty(int foodBreakfastMealsQty) { this.foodBreakfastMealsQty = Math.max(0, foodBreakfastMealsQty); }

    public int getFoodFruitsQty() { return foodFruitsQty; }
    public void setFoodFruitsQty(int foodFruitsQty) { this.foodFruitsQty = Math.max(0, foodFruitsQty); }

    public double getGymCharges() { return gymCharges; }
    public void setGymCharges(double gymCharges) { this.gymCharges = Math.max(0, gymCharges); }

    public double getSpaCharges() { return spaCharges; }
    public void setSpaCharges(double spaCharges) { this.spaCharges = Math.max(0, spaCharges); }

    public int getBevWineQty() { return bevWineQty; }
    public void setBevWineQty(int bevWineQty) { this.bevWineQty = Math.max(0, bevWineQty); }

    public int getBevAlcoholicQty() { return bevAlcoholicQty; }
    public void setBevAlcoholicQty(int bevAlcoholicQty) { this.bevAlcoholicQty = Math.max(0, bevAlcoholicQty); }

    public int getBevWaterQty() { return bevWaterQty; }
    public void setBevWaterQty(int bevWaterQty) { this.bevWaterQty = Math.max(0, bevWaterQty); }

    public int getBevJuiceQty() { return bevJuiceQty; }
    public void setBevJuiceQty(int bevJuiceQty) { this.bevJuiceQty = Math.max(0, bevJuiceQty); }

    public int getBevNonAlcoholicQty() { return bevNonAlcoholicQty; }
    public void setBevNonAlcoholicQty(int bevNonAlcoholicQty) { this.bevNonAlcoholicQty = Math.max(0, bevNonAlcoholicQty); }

    public int getGymSessions() { return gymSessions; }
    public void setGymSessions(int gymSessions) { this.gymSessions = Math.max(0, gymSessions); }

    public int getSpaSessions() { return spaSessions; }
    public void setSpaSessions(int spaSessions) { this.spaSessions = Math.max(0, spaSessions); }

    public double getBevWine() { return bevWine; }
    public void setBevWine(double bevWine) { this.bevWine = Math.max(0, bevWine); }

    public double getBevAlcoholic() { return bevAlcoholic; }
    public void setBevAlcoholic(double bevAlcoholic) { this.bevAlcoholic = Math.max(0, bevAlcoholic); }

    public double getBevWater() { return bevWater; }
    public void setBevWater(double bevWater) { this.bevWater = Math.max(0, bevWater); }

    public double getBevJuice() { return bevJuice; }
    public void setBevJuice(double bevJuice) { this.bevJuice = Math.max(0, bevJuice); }

    public double getBevNonAlcoholic() { return bevNonAlcoholic; }
    public void setBevNonAlcoholic(double bevNonAlcoholic) { this.bevNonAlcoholic = Math.max(0, bevNonAlcoholic); }

    public double calculateExtrasTotal() {
        double foodTotal = getFoodCharges();
        // If detailed food entries are present, prefer their sum
        double detailedFood = foodTibs + foodDorowet + foodBfe + foodBreakfastMeals + foodFruits;
        if (detailedFood > 0) foodTotal = detailedFood;

        double bevTotal = bevWine + bevAlcoholic + bevWater + bevJuice + bevNonAlcoholic;

        return foodTotal + bevTotal + getGymCharges() + getSpaCharges();
    }

    public double calculateGrandTotal(double pricePerNight) {
        return calculateTotal(pricePerNight) + calculateExtrasTotal() - calculateDiscount(pricePerNight);
    }

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
        return String.join(",",
            bookingId, guestId, roomNumber,
            checkIn.toString(), checkOut.toString(),
            String.valueOf(isPaid), notes.replace(",", ";"),
            String.valueOf(foodCharges), String.valueOf(gymCharges), String.valueOf(spaCharges),
            String.valueOf(foodTibs), String.valueOf(foodDorowet), String.valueOf(foodBfe), String.valueOf(foodBreakfastMeals), String.valueOf(foodFruits),
            String.valueOf(bevWine), String.valueOf(bevAlcoholic), String.valueOf(bevWater), String.valueOf(bevJuice), String.valueOf(bevNonAlcoholic),
            String.valueOf(foodTibsQty), String.valueOf(foodDorowetQty), String.valueOf(foodBfeQty), String.valueOf(foodBreakfastMealsQty), String.valueOf(foodFruitsQty),
            String.valueOf(bevWineQty), String.valueOf(bevAlcoholicQty), String.valueOf(bevWaterQty), String.valueOf(bevJuiceQty), String.valueOf(bevNonAlcoholicQty),
            String.valueOf(gymSessions), String.valueOf(spaSessions)
        );
    }

    /** Deserializes a Booking from a CSV line. */
    public static Booking fromFileString(String line) {
        String[] p = line.split(",", -1);
        Booking b = new Booking(p[0], p[1], p[2],
                LocalDate.parse(p[3]), LocalDate.parse(p[4]));
        b.isPaid = Boolean.parseBoolean(p[5]);
        b.notes = p.length > 6 ? p[6].replace(";", ",") : "";
        b.foodCharges = p.length > 7 && !p[7].isBlank() ? Double.parseDouble(p[7]) : 0.0;
        b.gymCharges = p.length > 8 && !p[8].isBlank() ? Double.parseDouble(p[8]) : 0.0;
        b.spaCharges = p.length > 9 && !p[9].isBlank() ? Double.parseDouble(p[9]) : 0.0;

        b.foodTibs = p.length > 10 && !p[10].isBlank() ? Double.parseDouble(p[10]) : 0.0;
        b.foodDorowet = p.length > 11 && !p[11].isBlank() ? Double.parseDouble(p[11]) : 0.0;
        b.foodBfe = p.length > 12 && !p[12].isBlank() ? Double.parseDouble(p[12]) : 0.0;
        b.foodBreakfastMeals = p.length > 13 && !p[13].isBlank() ? Double.parseDouble(p[13]) : 0.0;
        b.foodFruits = p.length > 14 && !p[14].isBlank() ? Double.parseDouble(p[14]) : 0.0;

        b.bevWine = p.length > 15 && !p[15].isBlank() ? Double.parseDouble(p[15]) : 0.0;
        b.bevAlcoholic = p.length > 16 && !p[16].isBlank() ? Double.parseDouble(p[16]) : 0.0;
        b.bevWater = p.length > 17 && !p[17].isBlank() ? Double.parseDouble(p[17]) : 0.0;
        b.bevJuice = p.length > 18 && !p[18].isBlank() ? Double.parseDouble(p[18]) : 0.0;
        b.bevNonAlcoholic = p.length > 19 && !p[19].isBlank() ? Double.parseDouble(p[19]) : 0.0;

        b.foodTibsQty = p.length > 20 && !p[20].isBlank() ? Integer.parseInt(p[20]) : 0;
        b.foodDorowetQty = p.length > 21 && !p[21].isBlank() ? Integer.parseInt(p[21]) : 0;
        b.foodBfeQty = p.length > 22 && !p[22].isBlank() ? Integer.parseInt(p[22]) : 0;
        b.foodBreakfastMealsQty = p.length > 23 && !p[23].isBlank() ? Integer.parseInt(p[23]) : 0;
        b.foodFruitsQty = p.length > 24 && !p[24].isBlank() ? Integer.parseInt(p[24]) : 0;

        b.bevWineQty = p.length > 25 && !p[25].isBlank() ? Integer.parseInt(p[25]) : 0;
        b.bevAlcoholicQty = p.length > 26 && !p[26].isBlank() ? Integer.parseInt(p[26]) : 0;
        b.bevWaterQty = p.length > 27 && !p[27].isBlank() ? Integer.parseInt(p[27]) : 0;
        b.bevJuiceQty = p.length > 28 && !p[28].isBlank() ? Integer.parseInt(p[28]) : 0;
        b.bevNonAlcoholicQty = p.length > 29 && !p[29].isBlank() ? Integer.parseInt(p[29]) : 0;

        b.gymSessions = p.length > 30 && !p[30].isBlank() ? Integer.parseInt(p[30]) : 0;
        b.spaSessions = p.length > 31 && !p[31].isBlank() ? Integer.parseInt(p[31]) : 0;
        return b;
    }

    @Override
    public String toString() {
        return getSummary();
    }
}
