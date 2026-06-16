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


public class ConsoleUI {
    private final HotelService service;
    private final Scanner scanner = new Scanner(System.in);

   
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
                System.out.println("3) Reception login");
                System.out.println("4) Admin login");
                System.out.println("5) Exit safely");
                System.out.print("Select: ");
                String opt = scanner.nextLine().trim();
                switch (opt) {
                    case "1" -> remoteAvailabilityFlow();
                    case "2" -> remoteReservationFlow();
                    case "3" -> receptionLogin();
                    case "4" -> adminLogin();
                    case "5" -> { System.out.println("Exiting safely..."); break outer; }
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
            System.out.println("Please visit reception on arrival to check in.");
        } catch (HotelException | EntityNotFoundException e) {
            System.err.println("Reservation failed: " + e.getMessage());
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
            System.out.println("6) Back");
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
                    case "6" -> { break admin; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (HotelException | EntityNotFoundException e) { System.err.println("Error: " + e.getMessage()); }
        }
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
}
