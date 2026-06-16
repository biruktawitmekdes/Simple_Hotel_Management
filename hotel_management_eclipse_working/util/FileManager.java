package util;

import model.Booking;
import model.Guest;
import model.Room;
import model.Staff;
import exception.HotelException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;


public class FileManager {

    private static final Path DATA_DIR = Path.of("data");
    private static final Path GUESTS = DATA_DIR.resolve("guests.txt");
    private static final Path ROOMS = DATA_DIR.resolve("rooms.txt");
    private static final Path BOOKINGS = DATA_DIR.resolve("bookings.txt");
    private static final Path STAFF = DATA_DIR.resolve("staff.txt");
    private static final Path ACTIVITY_LOG = DATA_DIR.resolve("activity.log");

    public static void initialize() throws HotelException {
        try {
            if (!Files.exists(DATA_DIR)) Files.createDirectories(DATA_DIR);
            if (!Files.exists(GUESTS)) Files.createFile(GUESTS);
            if (!Files.exists(ROOMS)) Files.createFile(ROOMS);
            if (!Files.exists(BOOKINGS)) Files.createFile(BOOKINGS);
            if (!Files.exists(STAFF)) Files.createFile(STAFF);
            if (!Files.exists(ACTIVITY_LOG)) Files.createFile(ACTIVITY_LOG);
        } catch (IOException e) {
            throw new HotelException("Failed to initialize data directory", e);
        }
    }

    public static void appendActivityLog(String entry) throws HotelException {
        try {
            Files.writeString(ACTIVITY_LOG, entry + System.lineSeparator(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new HotelException("Failed to write activity log", e);
        }
    }

    public static List<String> loadActivityLog() throws HotelException {
        List<String> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(ACTIVITY_LOG, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) if (!line.isBlank()) out.add(line);
        } catch (IOException e) { throw new HotelException("Failed to load activity logs", e); }
        return out;
    }

    public static List<Guest> loadGuests() throws HotelException {
        List<Guest> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(GUESTS, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) if (!line.isBlank()) out.add(Guest.fromFileString(line));
        } catch (IOException e) { throw new HotelException("Failed to load guests", e); }
        return out;
    }

    public static List<Room> loadRooms() throws HotelException {
        List<Room> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(ROOMS, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) if (!line.isBlank()) out.add(Room.fromFileString(line));
        } catch (IOException e) { throw new HotelException("Failed to load rooms", e); }
        return out;
    }

    public static List<Booking> loadBookings() throws HotelException {
        List<Booking> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(BOOKINGS, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) if (!line.isBlank()) out.add(Booking.fromFileString(line));
        } catch (IOException e) { throw new HotelException("Failed to load bookings", e); }
        return out;
    }

    public static List<Staff> loadStaff() throws HotelException {
        List<Staff> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(STAFF, StandardCharsets.UTF_8)) {
            String line;
            while ((line = r.readLine()) != null) if (!line.isBlank()) out.add(Staff.fromFileString(line));
        } catch (IOException e) { throw new HotelException("Failed to load staff", e); }
        return out;
    }

    private static void writeAll(Path path, List<String> lines) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (String l : lines) w.write(l + System.lineSeparator());
        }
    }

    public static void saveGuests(List<Guest> list) throws HotelException {
        try {
            List<String> lines = new ArrayList<>();
            for (Guest g : list) lines.add(g.toFileString());
            writeAll(GUESTS, lines);
        } catch (IOException e) { throw new HotelException("Failed to save guests", e); }
    }

    public static void saveRooms(List<Room> list) throws HotelException {
        try {
            List<String> lines = new ArrayList<>();
            for (Room r : list) lines.add(r.toFileString());
            writeAll(ROOMS, lines);
        } catch (IOException e) { throw new HotelException("Failed to save rooms", e); }
    }

    public static void saveBookings(List<Booking> list) throws HotelException {
        try {
            List<String> lines = new ArrayList<>();
            for (Booking b : list) lines.add(b.toFileString());
            writeAll(BOOKINGS, lines);
        } catch (IOException e) { throw new HotelException("Failed to save bookings", e); }
    }

    public static void saveStaff(List<Staff> list) throws HotelException {
        try {
            List<String> lines = new ArrayList<>();
            for (Staff s : list) lines.add(s.toFileString());
            writeAll(STAFF, lines);
        } catch (IOException e) { throw new HotelException("Failed to save staff", e); }
    }
}

