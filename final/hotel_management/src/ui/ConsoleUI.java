package ui;

import service.HotelService;
import model.Room;
import model.RoomType;
import model.Booking;
import model.Guest;
import exception.EntityNotFoundException;
import exception.HotelException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Terminal-based console UI for the hotel system.
 * Supports remote room availability, remote reservations,
 * front desk check-in/check-out, admin room management, and logs.
 */
public class ConsoleUI {
    private final HotelService service;
    private final Scanner scanner = new Scanner(System.in);

    // fixed credentials
    private final String RECV_EMAIL = "reception@gmail.com";
    private final String RECV_PASS = "Reception@123";
    private final String ADMIN_EMAIL = "admin@gmail.com";
    private final String ADMIN_PASS = "Admin@123";

    public ConsoleUI(HotelService service) { this.service = service; }

    public void start() {
        try {
            outer: while (true) {
                System.out.println("\n=== SUNRISE HOTEL - Terminal ===");
                System.out.println("1) Check room availability remotely");
                System.out.println("2) Reserve a room remotely");
                System.out.println("3) Order hotel services for an existing booking");
                System.out.println("4) Reception login");
                System.out.println("5) Admin login");
                System.out.println("6) Exit safely");
                System.out.print("Select: ");
                String opt = scanner.nextLine().trim();
                switch (opt) {
                    case "1" -> remoteAvailabilityFlow();
                    case "2" -> remoteReservationFlow();
                    case "3" -> guestServicesFlow();
                    case "4" -> receptionLogin();
                    case "5" -> adminLogin();
                    case "6" -> { System.out.println("Exiting safely..."); break outer; }
                    default -> System.out.println("Invalid option.");
                }
            }
        } finally {
            safeShutdown();
        }
    }

    private void remoteAvailabilityFlow() {
        System.out.println("\n--- Remote Room Availability ---");
        System.out.println("Available rooms:");
        List<Room> availableRooms = service.getAvailableRooms();
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms are currently available.");
            return;
        }
        availableRooms.forEach(room -> System.out.println(room.getSummary()));

        System.out.print("Enter room numbers to check (comma-separated), or press enter to view all available rooms: ");
        String input = scanner.nextLine().trim();
        if (input.isBlank()) return;

        List<String> roomNumbers = normalizeRoomNumbers(input);
        if (roomNumbers.isEmpty()) {
            System.out.println("No valid room numbers provided.");
            return;
        }

        List<Room> foundRooms = service.getRoomsByNumbers(roomNumbers);
        if (foundRooms.isEmpty()) {
            System.out.println("No matching rooms were found.");
            return;
        }
        System.out.println("Requested room statuses:");
        foundRooms.forEach(room -> System.out.println(room.getSummary()));
    }

    private void remoteReservationFlow() {
        System.out.println("\n--- Remote Room Reservation ---");
        List<Room> availableRooms = service.getAvailableRooms();
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms are available for reservation right now.");
            return;
        }
        availableRooms.forEach(room -> System.out.println(room.getSummary()));

        try {
            System.out.print("First name: "); String fn = scanner.nextLine().trim();
            System.out.print("Last name: "); String ln = scanner.nextLine().trim();
            System.out.print("Phone: "); String phone = scanner.nextLine().trim();
            System.out.print("Email: "); String email = scanner.nextLine().trim();
            System.out.print("Nationality: "); String nationality = scanner.nextLine().trim();
            System.out.print("ID/Passport: "); String idNumber = scanner.nextLine().trim();
            System.out.print("Room number to reserve: "); String roomNo = scanner.nextLine().trim();
            System.out.print("Check-in (YYYY-MM-DD): "); LocalDate ci = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("Check-out (YYYY-MM-DD): "); LocalDate co = LocalDate.parse(scanner.nextLine().trim());

            Booking booking = service.reserveRoomRemotely(fn, ln, phone, email, nationality, idNumber, roomNo, ci, co);
            service.saveAll();
            System.out.println("Reservation successful. Booking ID: " + booking.getId());
            System.out.println("If you want hotel services, use option 3 in the main menu after booking.");
        } catch (HotelException | EntityNotFoundException e) {
            System.err.println("Reservation failed: " + e.getMessage());
        }
    }

    private void guestServicesFlow() {
        System.out.println("\n--- Guest Hotel Services Menu ---");
        System.out.print("Enter your booking ID: ");
        String bookingId = scanner.nextLine().trim();
        try {
            System.out.println("Available hotel service prices:");
            System.out.printf("  Tibs = $%.2f each\n", service.getPrice("food.tibs"));
            System.out.printf("  Dorowet = $%.2f each\n", service.getPrice("food.dorowet"));
            System.out.printf("  BFE = $%.2f each\n", service.getPrice("food.bfe"));
            System.out.printf("  Breakfast meals = $%.2f each\n", service.getPrice("food.breakfast"));
            System.out.printf("  Fruits = $%.2f each\n", service.getPrice("food.fruits"));
            System.out.printf("  Wine = $%.2f each\n", service.getPrice("bev.wine"));
            System.out.printf("  Alcoholic drinks = $%.2f each\n", service.getPrice("bev.alcoholic"));
            System.out.printf("  Water = $%.2f each\n", service.getPrice("bev.water"));
            System.out.printf("  Juice = $%.2f each\n", service.getPrice("bev.juice"));
            System.out.printf("  Non-alcoholic drinks = $%.2f each\n", service.getPrice("bev.non_alcoholic"));
            System.out.printf("  Gym session = $%.2f each\n", service.getPrice("service.gym"));
            System.out.printf("  Spa session = $%.2f each\n", service.getPrice("service.spa"));

            System.out.println("Enter service quantities (integer, 0 if none):");
            System.out.print("  Tibs qty: "); int tibsQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Dorowet qty: "); int dorowetQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  BFE qty: "); int bfeQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Breakfast meals qty: "); int breakfastQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Fruits qty: "); int fruitsQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Wine qty: "); int wineQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Alcoholic drinks qty: "); int alcoholicQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Water qty: "); int waterQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Juice qty: "); int juiceQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Non-alcoholic drinks qty: "); int nonAlcoholicQty = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Gym sessions: "); int gymSessions = parseNonNegativeInt(scanner.nextLine().trim());
            System.out.print("  Spa sessions: "); int spaSessions = parseNonNegativeInt(scanner.nextLine().trim());

            service.addExtrasToBooking(bookingId,
                    tibsQty, dorowetQty, bfeQty, breakfastQty, fruitsQty,
                    wineQty, alcoholicQty, waterQty, juiceQty, nonAlcoholicQty,
                    gymSessions, spaSessions);
            service.saveAll();
            System.out.println("Hotel service selection recorded for booking " + bookingId + ".");
            System.out.println("Reception will see the service total during checkout.");
        } catch (Exception e) {
            System.err.println("Could not record hotel services: " + e.getMessage());
        }
    }

    private void receptionLogin() {
        System.out.print("Reception email: "); String email = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        if (!RECV_EMAIL.equals(email) || !RECV_PASS.equals(pass)) { System.out.println("Invalid credentials."); return; }
        System.out.println("Welcome, Receptionist.");
        receptionMenu();
    }

    private void receptionMenu() {
        inner: while (true) {
            System.out.println("\n--- Reception Menu ---");
            System.out.println("1) View all rooms");
            System.out.println("2) View all bookings");
            System.out.println("3) View pending bookings (not paid)");
            System.out.println("4) Accept payment for booking");
            System.out.println("5) Check-in guest");
            System.out.println("6) Check-out guest & generate invoice");
            System.out.println("7) Back");
            System.out.print("Select: ");
            String s = scanner.nextLine().trim();
            try {
                switch (s) {
                    case "1" -> service.getAllRooms().forEach(room -> System.out.println(room.getSummary()));
                    case "2" -> service.getAllBookings().forEach(booking -> System.out.println(booking.getSummary()));
                    case "3" -> service.getPendingBookings().forEach(booking -> System.out.println(booking.getSummary()));
                    case "4" -> {
                        System.out.print("Enter booking ID to accept payment: ");
                        String bid = scanner.nextLine().trim();
                        if (service.markBookingPaid(bid)) { service.saveAll(); System.out.println("Payment accepted."); }
                        else System.out.println("Booking not found or already paid.");
                    }
                    case "5" -> {
                        System.out.print("Enter booking ID to check in: ");
                        String bid = scanner.nextLine().trim();
                        if (service.checkInBooking(bid)) { service.saveAll(); System.out.println("Guest checked in successfully."); }
                    }
                    case "6" -> {
                        System.out.print("Enter booking ID to check out: ");
                        String bid = scanner.nextLine().trim();

                        String invoice = service.generateInvoice(bid);
                        double total = service.checkoutBooking(bid);
                        service.saveAll();
                        System.out.println(invoice);
                        System.out.printf("Total paid at check-out: $%.2f\n", total);
                    }
                    case "7" -> { break inner; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) { System.err.println("Error: " + e.getMessage()); }
        }
    }

    private void adminLogin() {
        System.out.print("Admin email: "); String email = scanner.nextLine().trim();
        System.out.print("Password: "); String pass = scanner.nextLine().trim();
        if (!ADMIN_EMAIL.equals(email) || !ADMIN_PASS.equals(pass)) { System.out.println("Invalid credentials."); return; }
        System.out.println("Welcome, Admin.");
        adminMenu();
    }

    private void adminMenu() {
        admin: while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1) Display all hotel units status");
            System.out.println("2) View system stats");
            System.out.println("3) Add new room");
            System.out.println("4) Delete a room");
            System.out.println("5) View system activity logs");
            System.out.println("6) Manage hotel services");
            System.out.println("7) Manage prices");
            System.out.println("8) Back");
            System.out.print("Select: ");
            String s = scanner.nextLine().trim();
            try {
                switch (s) {
                    case "1" -> service.getAllRooms().forEach(room -> System.out.println(room.getSummary()));
                    case "2" -> {
                        System.out.println("Total rooms: " + service.getTotalRooms());
                        System.out.println("Available: " + service.getAvailableCount());
                        System.out.println("Occupied: " + service.getOccupiedCount());
                        System.out.println("Total guests: " + service.getTotalGuests());
                        System.out.println("Total bookings: " + service.getTotalBookings());
                        System.out.println("Total staff: " + service.getTotalStaff());
                        System.out.println("Total revenue: $" + service.getTotalRevenue());
                    }
                    case "3" -> {
                        System.out.print("Room number: "); String rn = scanner.nextLine().trim();
                        System.out.print("Type (SINGLE,DOUBLE,SUITE,DELUXE,PRESIDENTIAL): ");
                        RoomType rt = RoomType.valueOf(scanner.nextLine().trim());
                        System.out.print("Floor: "); int floor = Integer.parseInt(scanner.nextLine().trim());
                        System.out.print("Has WiFi (true/false): "); boolean wifi = Boolean.parseBoolean(scanner.nextLine().trim());
                        System.out.print("Has AC (true/false): "); boolean ac = Boolean.parseBoolean(scanner.nextLine().trim());
                        service.addRoom(rn, rt, floor, wifi, ac);
                        service.saveAll();
                        System.out.println("Room added.");
                    }
                    case "4" -> {
                        System.out.print("Enter room number to delete: ");
                        String rn = scanner.nextLine().trim();
                        System.out.print("Are you sure you want to delete room " + rn + "? (yes/no): ");
                        String confirm = scanner.nextLine().trim().toLowerCase();
                        if (!confirm.equals("yes") && !confirm.equals("y")) {
                            System.out.println("Room deletion canceled.");
                        } else if (service.deleteRoom(rn)) {
                            service.saveAll();
                            System.out.println("Room deleted.");
                        }
                    }
                    case "5" -> {
                        List<String> logs = service.getActivityLogs();
                        if (logs.isEmpty()) {
                            System.out.println("No activity logs available.");
                        } else {
                            logs.forEach(System.out::println);
                        }
                    }
                    case "6" -> adminHotelServicesMenu();
                    case "7" -> {
                        System.out.println("--- Price List ---");
                        service.getAllPrices().forEach((k,v) -> System.out.printf("%s = $%.2f\n", k, v));
                        System.out.print("Enter item key to update price (or press enter to go back): ");
                        String key = scanner.nextLine().trim();
                        if (!key.isBlank()) {
                            if (!service.getAllPrices().containsKey(key)) {
                                System.out.println("Unknown key. No changes made.");
                            } else {
                                System.out.print("Enter new price for " + key + ": ");
                                double p = parseNonNegativeDouble(scanner.nextLine().trim());
                                service.setPrice(key, p);
                                try { service.saveAll(); } catch (HotelException ignored) {}
                                System.out.println("Price updated.");
                            }
                        }
                    }
                    case "8" -> { break admin; }
                    
                    default -> System.out.println("Invalid option.");
                }
            } catch (HotelException | EntityNotFoundException e) { System.err.println("Error: " + e.getMessage()); }
        }
    }

    private void adminHotelServicesMenu() {
        System.out.println("\n--- Admin Hotel Services Menu ---");
        System.out.println("Current hotel service items and prices:");
        service.getAllPrices().forEach((k, v) -> System.out.printf("  %s = $%.2f\n", k, v));
        System.out.print("Enter service key to update price (or press enter to go back): ");
        String key = scanner.nextLine().trim();
        if (key.isBlank()) {
            return;
        }
        if (!service.getAllPrices().containsKey(key)) {
            System.out.println("Unknown service key. No changes made.");
            return;
        }
        System.out.print("Enter new price for " + key + ": ");
        double newPrice = parseNonNegativeDouble(scanner.nextLine().trim());
        service.setPrice(key, newPrice);
        try { service.saveAll(); } catch (HotelException ignored) {}
        System.out.println("Service price updated for " + key + ".");
    }

    private void safeShutdown() {
        try {
            service.saveAll();
            System.out.println("System data saved safely.");
        } catch (HotelException e) {
            System.err.println("Could not save system data on exit: " + e.getMessage());
        }
    }

    private List<String> normalizeRoomNumbers(String input) {
        String[] tokens = input.split(",");
        List<String> rooms = new ArrayList<>();
        for (String token : tokens) {
            String item = token.trim();
            if (!item.isBlank()) rooms.add(item);
        }
        return rooms;
    }

    private int parseNonNegativeInt(String input) {
        try {
            int value = Integer.parseInt(input);
            return value < 0 ? 0 : value;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseNonNegativeDouble(String input) {
        try {
            double value = Double.parseDouble(input);
            return value < 0 ? 0.0 : value;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
