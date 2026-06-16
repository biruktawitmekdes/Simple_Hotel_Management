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

public class HotelService {

        private final Repository<Guest>   guestRepo   = new Repository<>();
    private final Repository<Room>    roomRepo    = new Repository<>();
    private final Repository<Booking> bookingRepo = new Repository<>();
    private final Repository<Staff>   staffRepo   = new Repository<>();

    private final List<String> activityLog = new ArrayList<>();

    
    private int bookingCounter = 1;
    private int guestCounter   = 1;
    private int staffCounter   = 1;

    private static final double LOYALTY_DISCOUNT = 0.15;
    private static final DateTimeFormatter LOG_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

   
    public void saveAll() throws HotelException {
        FileManager.saveGuests(guestRepo.getAll());
        FileManager.saveRooms(roomRepo.getAll());
        FileManager.saveBookings(bookingRepo.getAll());
        FileManager.saveStaff(staffRepo.getAll());
    }

    private void recordActivity(String event) {
        String entry = String.format("%s | %s", LocalDateTime.now().format(LOG_TS), event);
        activityLog.add(entry);
        try {
            FileManager.appendActivityLog(entry);
        } catch (HotelException ignored) {
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
        Guest guest = addGuest(firstName, lastName, phone, email, nationality, idNumber);
        Booking booking = createBooking(guest.getId(), roomNumber, checkIn, checkOut);
        recordActivity("Remote reservation created: " + booking.getId() + " for guest " + guest.getId() + " in room " + roomNumber);
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
        
        getGuest(guestId);

                Room room = getRoom(roomNumber);
        if (!room.isAvailable())
            throw new RoomNotAvailableException(roomNumber);

       
        if (!checkOut.isAfter(checkIn))
            throw new HotelException("Check-out date must be after check-in date.");

        String bid = String.format("B%04d", bookingCounter++);
        Booking booking = new Booking(bid, guestId, roomNumber, checkIn, checkOut);

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

        double subtotal = booking.calculateTotal(room.getPricePerNight());
        double discount = booking.calculateDiscount(room.getPricePerNight());
        double total = booking.calculateTotalWithDiscount(room.getPricePerNight());

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
                "Subtotal: $%.2f\n" +
                "Discount: $%.2f\n" +
                "Total Due: $%.2f\n" +
                "Paid: %s\n" +
                "----------------\n",
                booking.getId(), guest.getFullName(), guest.getId(), room.getRoomNumber(), room.getType().getDisplayName(),
                booking.getCheckIn(), booking.getCheckOut(), booking.getNights(), room.getPricePerNight(),
                subtotal, discount, total, booking.isPaid() ? "Yes" : "No");
    }

    public double checkoutBooking(String bookingId)
            throws EntityNotFoundException, HotelException {
        Booking booking = getBooking(bookingId);
        Room room = getRoom(booking.getRoomNumber());

        double subtotal = booking.calculateTotal(room.getPricePerNight());
        double discount = booking.calculateDiscount(room.getPricePerNight());
        double total = booking.calculateTotalWithDiscount(room.getPricePerNight());

        booking.setPaid(true);
        bookingRepo.update(bookingId, booking);

       
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepo.update(room.getRoomNumber(), room);

       
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

