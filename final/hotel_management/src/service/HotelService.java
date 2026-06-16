package service;

import model.*;
import util.Repository;
import util.FileManager;
import exception.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

/**
 * Central service class containing all business logic for the Hotel Management System.
 * Uses the generic Repository<T> for storing Guests, Rooms, Bookings, and Staff.
 * Demonstrates integration of OOP concepts: generics, exception handling, polymorphism.
 */
public class HotelService {

    // Generic repositories — demonstrates generics usage
    private final Repository<Guest>   guestRepo   = new Repository<>();
    private final Repository<Room>    roomRepo    = new Repository<>();
    private final Repository<Booking> bookingRepo = new Repository<>();
    private final Repository<Staff>   staffRepo   = new Repository<>();

    private final List<String> activityLog = new ArrayList<>();

    // Auto-increment counters for IDs
    private int bookingCounter = 1;
    private int guestCounter   = 1;
    private int staffCounter   = 1;

    private static final double LOYALTY_DISCOUNT = 0.15;
    private static final DateTimeFormatter LOG_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PriceList priceList = new PriceList();

    // ─────────────────────── INITIALIZATION ───────────────────────

    /** Loads all data from files into memory. */
    public void loadAll() throws HotelException {
        FileManager.initialize();

        guestRepo.clear();
        roomRepo.clear();
        bookingRepo.clear();
        staffRepo.clear();
        activityLog.clear();

        for (Guest g : FileManager.loadGuests())   guestRepo.add(g);
        for (Room r : FileManager.loadRooms())     roomRepo.add(r);
        for (Booking b : FileManager.loadBookings()) bookingRepo.add(b);
        for (Staff s : FileManager.loadStaff())    staffRepo.add(s);
        activityLog.addAll(FileManager.loadActivityLog());

        // Update counters based on loaded data
        guestRepo.getAll().forEach(g -> {
            try { int n = Integer.parseInt(g.getId().replaceAll("\\D", "")); if (n >= guestCounter) guestCounter = n + 1; } catch (Exception ignored) {}
        });
        staffRepo.getAll().forEach(s -> {
            try { int n = Integer.parseInt(s.getId().replaceAll("\\D", "")); if (n >= staffCounter) staffCounter = n + 1; } catch (Exception ignored) {}
        });
        bookingRepo.getAll().forEach(b -> {
            try { int n = Integer.parseInt(b.getId().replaceAll("\\D", "")); if (n >= bookingCounter) bookingCounter = n + 1; } catch (Exception ignored) {}
        });
    }

    /** Saves all data to files. */
    public void saveAll() throws HotelException {
        FileManager.saveGuests(guestRepo.getAll());
        FileManager.saveRooms(roomRepo.getAll());
        FileManager.saveBookings(bookingRepo.getAll());
        FileManager.saveStaff(staffRepo.getAll());
    }

    public double getPrice(String key) { return priceList.getPrice(key); }

    public void setPrice(String key, double price) {
        priceList.setPrice(key, price);
        recordActivity("Price updated: " + key + " = " + price);
    }

    public Map<String, Double> getAllPrices() { return priceList.getAllPrices(); }

    private void recordActivity(String event) {
        String entry = String.format("%s | %s", LocalDateTime.now().format(LOG_TS), event);
        activityLog.add(entry);
        try {
            FileManager.appendActivityLog(entry);
        } catch (HotelException ignored) {
            // Logging should not break business flow.
        }
    }

    public List<Room> getRoomsByNumbers(List<String> roomNumbers) {
        return roomNumbers.stream()
                .map(String::trim)
                .filter(rn -> !rn.isBlank())
                .map(roomRepo::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public Booking reserveRoomRemotely(String firstName, String lastName, String phone,
                                      String email, String nationality, String idNumber,
                                      String roomNumber, LocalDate checkIn, LocalDate checkOut)
            throws HotelException, EntityNotFoundException {
        return reserveRoomRemotely(firstName, lastName, phone, email, nationality, idNumber,
                roomNumber, checkIn, checkOut,
                0, 0, 0, 0, 0,
                0, 0, 0, 0, 0,
                0, 0);
    }

    public Booking reserveRoomRemotely(String firstName, String lastName, String phone,
                                      String email, String nationality, String idNumber,
                                      String roomNumber, LocalDate checkIn, LocalDate checkOut,
                                      int tibsQty, int dorowetQty, int bfeQty, int breakfastMealsQty, int fruitsQty,
                                      int wineQty, int alcoholicQty, int waterQty, int juiceQty, int nonAlcoholicQty,
                                      int gymSessions, int spaSessions)
            throws HotelException, EntityNotFoundException {
        Guest guest = addGuest(firstName, lastName, phone, email, nationality, idNumber);
        Booking booking = createBooking(guest.getId(), roomNumber, checkIn, checkOut);
        addExtrasToBooking(booking.getId(), tibsQty, dorowetQty, bfeQty, breakfastMealsQty, fruitsQty,
                wineQty, alcoholicQty, waterQty, juiceQty, nonAlcoholicQty,
                gymSessions, spaSessions);
        recordActivity("Remote reservation with extras created: " + booking.getId() + " for guest "
                + guest.getId() + " in room " + roomNumber);
        return booking;
    }

    // ─────────────────────── GUEST OPERATIONS ───────────────────────

    public Guest addGuest(String firstName, String lastName, String phone,
                          String email, String nationality, String idNumber) throws HotelException {
        String id = String.format("G%03d", guestCounter++);
        Guest g = new Guest(id, firstName, lastName, phone, email, nationality, idNumber);
        if (!g.validate()) throw new HotelException("Invalid guest data provided.");
        guestRepo.add(g);
        recordActivity("Guest added: " + g.getId() + " (" + g.getFullName() + ")");
        return g;
    }

    public List<Guest> getAllGuests() { return guestRepo.getAll(); }

    public Guest getGuest(String id) throws EntityNotFoundException {
        return guestRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Guest", id));
    }

    public List<Guest> searchGuests(String keyword) { return guestRepo.search(keyword); }

    public boolean updateGuest(String id, String phone, String email, String nationality)
            throws EntityNotFoundException {
        Guest g = getGuest(id);
        if (phone != null && !phone.isBlank()) g.setPhone(phone);
        if (email != null && !email.isBlank()) g.setEmail(email);
        if (nationality != null && !nationality.isBlank()) g.setNationality(nationality);
        return guestRepo.update(id, g);
    }

    public boolean deleteGuest(String id) throws EntityNotFoundException {
        getGuest(id); // throws if not found
        boolean removed = guestRepo.removeById(id);
        if (removed) recordActivity("Guest deleted: " + id);
        return removed;
    }

    // ─────────────────────── ROOM OPERATIONS ───────────────────────

    public Room addRoom(String roomNumber, RoomType type, int floor,
                        boolean wifi, boolean ac) throws HotelException {
        if (roomRepo.findById(roomNumber).isPresent())
            throw new HotelException("Room " + roomNumber + " already exists.");
        Room r = new Room(roomNumber, type, floor, wifi, ac);
        roomRepo.add(r);
        recordActivity("Room added: " + roomNumber + " type " + type.name());
        return r;
    }

    public List<Room> getAllRooms() { return roomRepo.getAll(); }

    public Room getRoom(String roomNumber) throws EntityNotFoundException {
        return roomRepo.findById(roomNumber)
                .orElseThrow(() -> new EntityNotFoundException("Room", roomNumber));
    }

    public List<Room> getAvailableRooms() {
        return roomRepo.getAll().stream()
                .filter(Room::isAvailable)
                .collect(Collectors.toList());
    }

    public List<Room> searchRooms(String keyword) { return roomRepo.search(keyword); }

    public boolean updateRoomStatus(String roomNumber, RoomStatus status)
            throws EntityNotFoundException {
        Room r = getRoom(roomNumber);
        r.setStatus(status);
        return roomRepo.update(roomNumber, r);
    }

    public boolean deleteRoom(String roomNumber) throws EntityNotFoundException, HotelException {
        Room room = getRoom(roomNumber);
        if (!room.isAvailable())
            throw new HotelException("Room " + roomNumber + " cannot be deleted while it is occupied or reserved.");
        boolean removed = roomRepo.removeById(roomNumber);
        if (removed) recordActivity("Room deleted: " + roomNumber);
        return removed;
    }

    // ─────────────────────── BOOKING OPERATIONS ───────────────────────

    public Booking createBooking(String guestId, String roomNumber,
                                 LocalDate checkIn, LocalDate checkOut)
            throws HotelException, EntityNotFoundException {
        // Validate guest exists
        getGuest(guestId);

        // Validate room exists and is available
        Room room = getRoom(roomNumber);
        if (!room.isAvailable())
            throw new RoomNotAvailableException(roomNumber);

        // Validate dates
        if (!checkOut.isAfter(checkIn))
            throw new HotelException("Check-out date must be after check-in date.");

        String bid = String.format("B%04d", bookingCounter++);
        Booking booking = new Booking(bid, guestId, roomNumber, checkIn, checkOut);

        // Mark room as reserved until check-in
        room.setStatus(RoomStatus.RESERVED);
        roomRepo.update(roomNumber, room);

        bookingRepo.add(booking);
        recordActivity("Booking reserved: " + booking.getId() + " for guest " + guestId + " in room " + roomNumber);
        return booking;
    }

    public boolean checkInBooking(String bookingId) throws EntityNotFoundException, HotelException {
        Booking booking = getBooking(bookingId);
        Room room = getRoom(booking.getRoomNumber());
        if (room.getStatus() != RoomStatus.RESERVED)
            throw new HotelException("Room cannot be checked in unless it is reserved.");
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepo.update(room.getRoomNumber(), room);
        recordActivity("Checked in booking: " + bookingId + " room " + room.getRoomNumber());
        return true;
    }

    public List<Booking> getAllBookings() { return bookingRepo.getAll(); }

    public Booking getBooking(String id) throws EntityNotFoundException {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking", id));
    }

    public List<Booking> getBookingsByGuest(String guestId) {
        return bookingRepo.getAll().stream()
                .filter(b -> b.getGuestId().equalsIgnoreCase(guestId))
                .collect(Collectors.toList());
    }

    public String generateInvoice(String bookingId) throws EntityNotFoundException {
        Booking booking = getBooking(bookingId);
        Room room = getRoom(booking.getRoomNumber());
        Guest guest = getGuest(booking.getGuestId());

        double tibsPrice = getPrice("food.tibs");
        double dorowetPrice = getPrice("food.dorowet");
        double bfePrice = getPrice("food.bfe");
        double breakfastPrice = getPrice("food.breakfast");
        double fruitsPrice = getPrice("food.fruits");

        double winePrice = getPrice("bev.wine");
        double alcoholicPrice = getPrice("bev.alcoholic");
        double waterPrice = getPrice("bev.water");
        double juicePrice = getPrice("bev.juice");
        double nonAlcoholicPrice = getPrice("bev.non_alcoholic");

        double tibsTotal = booking.getFoodTibsQty() * tibsPrice;
        double dorowetTotal = booking.getFoodDorowetQty() * dorowetPrice;
        double bfeTotal = booking.getFoodBfeQty() * bfePrice;
        double breakfastTotal = booking.getFoodBreakfastMealsQty() * breakfastPrice;
        double fruitsTotal = booking.getFoodFruitsQty() * fruitsPrice;

        double wineTotal = booking.getBevWineQty() * winePrice;
        double alcoholicTotal = booking.getBevAlcoholicQty() * alcoholicPrice;
        double waterTotal = booking.getBevWaterQty() * waterPrice;
        double juiceTotal = booking.getBevJuiceQty() * juicePrice;
        double nonAlcoholicTotal = booking.getBevNonAlcoholicQty() * nonAlcoholicPrice;

        double gymPrice = getPrice("service.gym");
        double spaPrice = getPrice("service.spa");

        double gymTotal = booking.getGymSessions() * gymPrice;
        double spaTotal = booking.getSpaSessions() * spaPrice;

        double extrasTotal = tibsTotal + dorowetTotal + bfeTotal + breakfastTotal + fruitsTotal
                + wineTotal + alcoholicTotal + waterTotal + juiceTotal + nonAlcoholicTotal
                + gymTotal + spaTotal;

        booking.setFoodCharges(tibsTotal + dorowetTotal + bfeTotal + breakfastTotal + fruitsTotal + wineTotal + alcoholicTotal + waterTotal + juiceTotal + nonAlcoholicTotal);
        booking.setGymCharges(gymTotal);
        booking.setSpaCharges(spaTotal);
        bookingRepo.update(bookingId, booking);

        double roomSubtotal = booking.calculateTotal(room.getPricePerNight());
        double discount = booking.calculateDiscount(room.getPricePerNight());
        double total = roomSubtotal + extrasTotal - discount;

        return String.format(
                "\n--- INVOICE ---\n" +
                "Booking ID: %s\n" +
                "Guest: %s (%s)\n" +
                "Room: %s\n" +
                "Type: %s\n" +
                "Check-in: %s\n" +
                "Check-out: %s\n" +
                "Nights: %d\n" +
                "Price/night: $%.2f\n" +
                "Room subtotal: $%.2f\n" +
                "-- Food usage --\n" +
                "  Tibs: %d x $%.2f = $%.2f\n" +
                "  Dorowet: %d x $%.2f = $%.2f\n" +
                "  BFE: %d x $%.2f = $%.2f\n" +
                "  Breakfast meals: %d x $%.2f = $%.2f\n" +
                "  Fruits: %d x $%.2f = $%.2f\n" +
                "-- Beverages usage --\n" +
                "  Wine: %d x $%.2f = $%.2f\n" +
                "  Alcoholic: %d x $%.2f = $%.2f\n" +
                "  Water: %d x $%.2f = $%.2f\n" +
                "  Juice: %d x $%.2f = $%.2f\n" +
                "  Non-alcoholic: %d x $%.2f = $%.2f\n" +
                "Gym sessions: %d x $%.2f = $%.2f\n" +
                "Spa sessions: %d x $%.2f = $%.2f\n" +
                "Hotel services usage total: $%.2f\n" +
                "Discount: $%.2f\n" +
                "Total Due: $%.2f\n" +
                "Paid: %s\n" +
                "----------------\n",
                booking.getId(), guest.getFullName(), guest.getId(), room.getRoomNumber(), room.getType().getDisplayName(),
                booking.getCheckIn(), booking.getCheckOut(), booking.getNights(), room.getPricePerNight(),
                roomSubtotal,
                booking.getFoodTibsQty(), tibsPrice, tibsTotal,
                booking.getFoodDorowetQty(), dorowetPrice, dorowetTotal,
                booking.getFoodBfeQty(), bfePrice, bfeTotal,
                booking.getFoodBreakfastMealsQty(), breakfastPrice, breakfastTotal,
                booking.getFoodFruitsQty(), fruitsPrice, fruitsTotal,
                booking.getBevWineQty(), winePrice, wineTotal,
                booking.getBevAlcoholicQty(), alcoholicPrice, alcoholicTotal,
                booking.getBevWaterQty(), waterPrice, waterTotal,
                booking.getBevJuiceQty(), juicePrice, juiceTotal,
                booking.getBevNonAlcoholicQty(), nonAlcoholicPrice, nonAlcoholicTotal,
                booking.getGymSessions(), gymPrice, gymTotal,
                booking.getSpaSessions(), spaPrice, spaTotal,
                extrasTotal, discount, total, booking.isPaid() ? "Yes" : "No");
    }

    public void addExtrasToBooking(String bookingId,
                                   int tibsQty, int dorowetQty, int bfeQty, int breakfastMealsQty, int fruitsQty,
                                   int wineQty, int alcoholicQty, int waterQty, int juiceQty, int nonAlcoholicQty,
                                   int gymSessions, int spaSessions)
            throws EntityNotFoundException {
        Booking b = getBooking(bookingId);
        b.setFoodTibsQty(tibsQty);
        b.setFoodDorowetQty(dorowetQty);
        b.setFoodBfeQty(bfeQty);
        b.setFoodBreakfastMealsQty(breakfastMealsQty);
        b.setFoodFruitsQty(fruitsQty);

        b.setBevWineQty(wineQty);
        b.setBevAlcoholicQty(alcoholicQty);
        b.setBevWaterQty(waterQty);
        b.setBevJuiceQty(juiceQty);
        b.setBevNonAlcoholicQty(nonAlcoholicQty);

        b.setGymSessions(gymSessions);
        b.setSpaSessions(spaSessions);

        double tibsTotal = tibsQty * getPrice("food.tibs");
        double dorowetTotal = dorowetQty * getPrice("food.dorowet");
        double bfeTotal = bfeQty * getPrice("food.bfe");
        double breakfastTotal = breakfastMealsQty * getPrice("food.breakfast");
        double fruitsTotal = fruitsQty * getPrice("food.fruits");

        double wineTotal = wineQty * getPrice("bev.wine");
        double alcoholicTotal = alcoholicQty * getPrice("bev.alcoholic");
        double waterTotal = waterQty * getPrice("bev.water");
        double juiceTotal = juiceQty * getPrice("bev.juice");
        double nonAlcoholicTotal = nonAlcoholicQty * getPrice("bev.non_alcoholic");

        double gymTotal = gymSessions * getPrice("service.gym");
        double spaTotal = spaSessions * getPrice("service.spa");

        b.setFoodTibs(tibsTotal);
        b.setFoodDorowet(dorowetTotal);
        b.setFoodBfe(bfeTotal);
        b.setFoodBreakfastMeals(breakfastTotal);
        b.setFoodFruits(fruitsTotal);

        b.setBevWine(wineTotal);
        b.setBevAlcoholic(alcoholicTotal);
        b.setBevWater(waterTotal);
        b.setBevJuice(juiceTotal);
        b.setBevNonAlcoholic(nonAlcoholicTotal);

        b.setGymCharges(gymTotal);
        b.setSpaCharges(spaTotal);
        b.setFoodCharges(tibsTotal + dorowetTotal + bfeTotal + breakfastTotal + fruitsTotal
                + wineTotal + alcoholicTotal + waterTotal + juiceTotal + nonAlcoholicTotal);

        bookingRepo.update(bookingId, b);
    }

    public double checkoutBooking(String bookingId)
            throws EntityNotFoundException, HotelException {
        return checkoutBooking(bookingId, 0.0, 0.0, 0.0);
    }

    public double checkoutBooking(String bookingId, double foodCharges, double gymCharges, double spaCharges)
            throws EntityNotFoundException, HotelException {
        Booking booking = getBooking(bookingId);
        Room room = getRoom(booking.getRoomNumber());

        booking.setFoodCharges(foodCharges);
        booking.setGymCharges(gymCharges);
        booking.setSpaCharges(spaCharges);

        double total = booking.calculateGrandTotal(room.getPricePerNight());

        booking.setPaid(true);
        bookingRepo.update(bookingId, booking);

        // Free the room
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepo.update(room.getRoomNumber(), room);

        // Increment guest stays
        Optional<Guest> guestOpt = guestRepo.findById(booking.getGuestId());
        guestOpt.ifPresent(g -> { g.incrementStays(); guestRepo.update(g.getId(), g); });

        recordActivity("Checked out booking: " + bookingId + " total $" + total);
        return total;
    }

    public List<Booking> getPendingBookings() {
        return bookingRepo.getAll().stream().filter(b -> !b.isPaid()).collect(Collectors.toList());
    }

    public boolean markBookingPaid(String bookingId) throws EntityNotFoundException {
        Booking b = getBooking(bookingId);
        if (b.isPaid()) return false;
        b.setPaid(true);
        boolean updated = bookingRepo.update(bookingId, b);
        if (updated) recordActivity("Payment accepted for booking: " + bookingId);
        return updated;
    }

    public boolean cancelBooking(String bookingId)
            throws EntityNotFoundException {
        Booking booking = getBooking(bookingId);
        try {
            Room room = getRoom(booking.getRoomNumber());
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepo.update(room.getRoomNumber(), room);
        } catch (EntityNotFoundException ignored) {}
        recordActivity("Canceled booking: " + bookingId);
        return bookingRepo.removeById(bookingId);
    }

    // ─────────────────────── STAFF OPERATIONS ───────────────────────

    public Staff addStaff(String firstName, String lastName, String phone,
                          String email, String department, String position, double salary)
            throws HotelException {
        String id = String.format("S%03d", staffCounter++);
        Staff s = new Staff(id, firstName, lastName, phone, email, department, position, salary);
        if (!s.validate()) throw new HotelException("Invalid staff data provided.");
        staffRepo.add(s);
        recordActivity("Staff added: " + s.getId());
        return s;
    }

    public List<Staff> getAllStaff() { return staffRepo.getAll(); }

    public Staff getStaff(String id) throws EntityNotFoundException {
        return staffRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Staff", id));
    }

    public List<Staff> searchStaff(String keyword) { return staffRepo.search(keyword); }

    public boolean deleteStaff(String id) throws EntityNotFoundException {
        getStaff(id);
        boolean removed = staffRepo.removeById(id);
        if (removed) recordActivity("Staff deleted: " + id);
        return removed;
    }

    public List<String> getActivityLogs() { return new ArrayList<>(activityLog); }

    // ─────────────────────── STATISTICS ───────────────────────

    public int getTotalRooms()     { return roomRepo.count(); }
    public int getAvailableCount() { return (int) roomRepo.getAll().stream().filter(Room::isAvailable).count(); }
    public int getOccupiedCount()  { return (int) roomRepo.getAll().stream().filter(r -> r.getStatus() == RoomStatus.OCCUPIED).count(); }
    public int getTotalGuests()    { return guestRepo.count(); }
    public int getTotalBookings()  { return bookingRepo.count(); }
    public int getTotalStaff()     { return staffRepo.count(); }

    public double getTotalRevenue() {
        return bookingRepo.getAll().stream()
                .filter(Booking::isPaid)
                .mapToDouble(b -> {
                    try {
                        Room r = getRoom(b.getRoomNumber());
                        return b.calculateTotalWithDiscount(r.getPricePerNight());
                    } catch (Exception e) { return 0; }
                }).sum();
    }
}

