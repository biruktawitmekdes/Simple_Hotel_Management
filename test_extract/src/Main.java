import model.*;
import service.HotelService;
import ui.ConsoleUI;
import exception.*;
import util.FileManager;

import java.io.File;
import java.time.LocalDate;

/**
 * ============================================================
 *         SUNRISE HOTEL MANAGEMENT SYSTEM
 * ============================================================
 * Submitted to : Abdisa K.
 * Project      : Hotel Management System
 * Language     : Java (OOP)
 * 
 * OOP Concepts Demonstrated:
 *  - Classes & Objects       ✔   (Guest, Room, Booking, Staff…)
 *  - Encapsulation           ✔   (private fields + getters/setters)
 *  - Inheritance             ✔   (Guest, Staff extend Person)
 *  - Polymorphism            ✔   (method overriding, dynamic binding)
 *  - Abstraction             ✔   (abstract class Person)
 *  - Interface               ✔   (Manageable interface)
 *  - Generics                ✔   (Repository<T extends Manageable>)
 *  - Enum                    ✔   (RoomType, RoomStatus)
 *  - Exception Handling      ✔   (HotelException, EntityNotFoundException, RoomNotAvailableException)
 *  - File Handling           ✔   (FileManager: read/write .txt files)
 *  - Packages                ✔   (model, service, util, exception, ui)
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {
        HotelService service = new HotelService();

        // ── Load existing data from files ──
        try {
            service.loadAll();
        } catch (HotelException e) {
            System.err.println("Warning: Could not load saved data. Starting fresh. (" + e.getMessage() + ")");
        }

        // ── Seed demo data if no rooms/guests exist ──
        if (service.getTotalRooms() == 0) {
            seedDemoData(service);
        }

        // ── Launch console UI ──
        ConsoleUI ui = new ConsoleUI(service);
        ui.start();
    }

    /**
     * Seeds the system with sample data to demonstrate all features
     * without requiring manual input on first run.
     */
    private static void seedDemoData(HotelService service) {
        System.out.println("\n  [INFO] Seeding demo data for first run...");
        try {
            // Add rooms
            service.addRoom("101", RoomType.SINGLE,       1, true, true);
            service.addRoom("102", RoomType.SINGLE,       1, true, false);
            service.addRoom("201", RoomType.DOUBLE,       2, true, true);
            service.addRoom("202", RoomType.DOUBLE,       2, true, true);
            service.addRoom("301", RoomType.SUITE,        3, true, true);
            service.addRoom("401", RoomType.DELUXE,       4, true, true);
            service.addRoom("501", RoomType.PRESIDENTIAL, 5, true, true);

            // Add guests
            Guest g1 = service.addGuest("Abebe",  "Girma",   "0911234567", "abebe@mail.com",  "Ethiopian",    "ET123456");
            Guest g2 = service.addGuest("Sara",   "Tadesse", "0922345678", "sara@mail.com",   "Ethiopian",    "ET789012");
            Guest g3 = service.addGuest("James",  "Miller",  "0933456789", "james@mail.com",  "American",     "US987654");
            Guest g4 = service.addGuest("Fatima", "Ali",     "0944567890", "fatima@mail.com", "Kenyan",       "KE456789");

            // Add staff
            service.addStaff("Kebede",  "Worku",  "0955000001", "kebede@hotel.com",  "Front Desk",   "Receptionist",    2500.0);
            service.addStaff("Tigist",  "Haile",  "0955000002", "tigist@hotel.com",  "Housekeeping", "Room Attendant",  1800.0);
            service.addStaff("Michael", "Tesfaye","0955000003", "michael@hotel.com", "Restaurant",   "Head Chef",       3200.0);
            service.addStaff("Meron",   "Bekele", "0955000004", "meron@hotel.com",   "Management",   "Hotel Manager",   5000.0);

            // Create sample bookings
            Booking b1 = service.createBooking(g1.getId(), "201",
                    LocalDate.now().minusDays(3), LocalDate.now().plusDays(2));

            Booking b2 = service.createBooking(g3.getId(), "301",
                    LocalDate.now(), LocalDate.now().plusDays(5));

            // Checkout one booking to show paid revenue
            service.checkoutBooking(b1.getId());

            // Save seeded data
            service.saveAll();
            System.out.println("  [INFO] Demo data loaded successfully!\n");

        } catch (HotelException | EntityNotFoundException e) {
            System.err.println("  [WARN] Seed error: " + e.getMessage());
        }
    }
}
