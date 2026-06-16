# Hotel Management System - Detailed Class Documentation

## Project Overview
A comprehensive Hotel Management System built in Java demonstrating Object-Oriented Programming principles. The system manages guests, staff, rooms, and bookings with advanced features like pricing, activity logging, and multi-service charges.

---

## MODEL LAYER DOCUMENTATION

### 1. Manageable Interface
**Purpose**: Contract for all manageable entities in the system

**Methods**:
- `String getId()`: Returns unique identifier for the entity
  - Used as primary key in repositories
  - Examples: "G001" (guest), "102" (room number), "BK2024001" (booking)
  
- `String getSummary()`: Returns formatted display string
  - Used for listing and console output
  - Each class provides columnar/CSV-formatted output
  
- `boolean validate()`: Validates entity state
  - Called before persistence operations
  - Each class defines its own validation rules
  - Returns true if all required fields are valid

**Implementations**: Person, Room, Booking

---

### 2. Person (Abstract Class)
**Purpose**: Base class for all persons (Guests and Staff)

**Fields**:
- `String id`: Unique identifier (e.g., "G001", "S001")
- `String firstName`: Person's first name (required, non-blank)
- `String lastName`: Person's family name (required, non-blank)
- `String phone`: Contact phone number (required, non-blank)
- `String email`: Contact email address (required, non-blank)

**Constructor**:
- `Person(String id, String firstName, String lastName, String phone, String email)`
  - Initializes all base person fields
  - No validation at construction time
  - Subclasses must call super() to inherit these fields

**Key Methods**:
- `String getId()`: Returns person's unique identifier
- `String getFirstName()` / `setFirstName(String)`: Access to first name
- `String getLastName()` / `setLastName(String)`: Access to last name
- `String getFullName()`: Returns "FirstName LastName" concatenation
- `String getPhone()` / `setPhone(String)`: Access to phone number
- `String getEmail()` / `setEmail(String)`: Access to email address
- `boolean validate()`: Validates id, firstName, lastName, phone are not blank
- `abstract String getRole()`: POLYMORPHIC - subclasses return "Guest" or "Staff"
- `String toString()`: Returns "[ID] FullName | Phone: ... | Email: ..."

**Design Principles**:
- ENCAPSULATION: All fields are private with public accessors
- INHERITANCE: Template for Guest and Staff subclasses
- ABSTRACTION: getRole() is abstract, forcing subclasses to define behavior
- POLYMORPHISM: Each subclass implements getRole() differently

---

### 3. Guest (Extends Person)
**Purpose**: Represents a hotel guest/visitor

**Additional Fields**:
- `String nationality`: Guest's country of origin
- `String idNumber`: National ID or Passport number (required for guest validation)
- `int totalStays`: Counter for number of previous stays (starts at 0)

**Constructor**:
- `Guest(String id, String firstName, String lastName, String phone, String email, String nationality, String idNumber)`
  - Calls super() to initialize Person fields
  - Sets nationality and idNumber
  - Initializes totalStays to 0

**Key Methods**:
- `String getNationality()` / `setNationality(String)`: Access to nationality
- `String getIdNumber()` / `setIdNumber(String)`: Access to ID/passport number
- `int getTotalStays()`: Returns total stay count
- `void incrementStays()`: Increments totalStays by 1 (called when guest checks out)
- `@Override String getRole()`: Returns "Guest" (polymorphic implementation)
- `@Override String getSummary()`: 
  - Format: "GUEST | ID: ... | FullName | Phone: ... | Nationality: ..."
  - Used for listing guests in the UI
- `@Override boolean validate()`: 
  - Calls super.validate() for base validation
  - Additionally validates that idNumber is not blank
- `String toFileString()`: Serializes guest to CSV format for file storage
  - Format: id,firstName,lastName,phone,email,nationality,idNumber,totalStays
- `static Guest fromFileString(String line)`: Deserializes guest from CSV line

**Inheritance Relationship**:
- Extends Person to inherit id, firstName, lastName, phone, email
- Guest-specific fields: nationality, idNumber, totalStays
- Overrides: getRole(), getSummary(), validate()

---

### 4. Staff (Extends Person)
**Purpose**: Represents a hotel staff member/employee

**Additional Fields**:
- `String department`: Staff department (e.g., "Housekeeping", "Front Desk", "Management")
- `String position`: Job position/title (e.g., "Manager", "Attendant", "Receptionist")
- `double salary`: Monthly/annual salary in currency

**Constructor**:
- `Staff(String id, String firstName, String lastName, String phone, String email, String department, String position, double salary)`
  - Calls super() to initialize Person fields
  - Sets department, position, and salary

**Key Methods**:
- `String getDepartment()` / `setDepartment(String)`: Access to department
- `String getPosition()` / `setPosition(String)`: Access to job position
- `double getSalary()` / `setSalary(double)`: Access to salary
- `@Override String getRole()`: Returns "Staff" (polymorphic implementation)
- `@Override String getSummary()`:
  - Format: "STAFF | ID: ... | FullName | Dept: ... | Position: ..."
  - Used for listing staff in the UI
- `String toFileString()`: Serializes staff to CSV format
  - Format: id,firstName,lastName,phone,email,department,position,salary
- `static Staff fromFileString(String line)`: Deserializes staff from CSV line

**Inheritance Relationship**:
- Extends Person to inherit id, firstName, lastName, phone, email
- Staff-specific fields: department, position, salary
- Overrides: getRole(), getSummary()

---

### 5. RoomType (Enum)
**Purpose**: Enumeration of available room types with pricing

**Enum Constants**:
- `SINGLE("Single", 50.0)`: Single bed room, $50/night base price
- `DOUBLE("Double", 80.0)`: Double bed room, $80/night base price
- `SUITE("Suite", 150.0)`: Suite with living area, $150/night base price
- `DELUXE("Deluxe", 200.0)`: Deluxe room with premium amenities, $200/night base price
- `PRESIDENTIAL("Presidential", 500.0)`: Luxury presidential suite, $500/night base price

**Fields** (for each constant):
- `String displayName`: Human-readable name for UI display
- `double basePrice`: Base nightly rate in currency

**Methods**:
- `String getDisplayName()`: Returns the display name
- `double getBasePrice()`: Returns the base price per night
- `@Override String toString()`: Returns displayName (used in string conversion)

**Usage**:
- Each Room has a RoomType that determines its base price
- Prices are used in booking calculations
- Enum ensures type safety and prevents invalid room types

---

### 6. RoomStatus (Enum)
**Purpose**: Enumeration of room availability states

**Enum Constants**:
- `AVAILABLE("Available")`: Room is ready and can be booked
- `OCCUPIED("Occupied")`: Room is currently occupied by a guest
- `UNDER_MAINTENANCE("Under Maintenance")`: Room is being serviced and unavailable
- `RESERVED("Reserved")`: Room is reserved for upcoming booking

**Fields** (for each constant):
- `String displayName`: Human-readable status description

**Methods**:
- `String getDisplayName()`: Returns the display name
- `@Override String toString()`: Returns displayName

**State Transitions**:
- AVAILABLE → OCCUPIED (guest checks in)
- OCCUPIED → AVAILABLE (guest checks out)
- AVAILABLE → RESERVED (booking created)
- RESERVED → OCCUPIED (guest checks in)
- Any state → UNDER_MAINTENANCE (maintenance needed)
- UNDER_MAINTENANCE → AVAILABLE (maintenance complete)

---

### 7. Room (Implements Manageable)
**Purpose**: Represents a physical hotel room

**Fields**:
- `String roomNumber`: Room identifier (e.g., "101", "202") - Primary Key
- `RoomType type`: Room type (SINGLE, DOUBLE, SUITE, DELUXE, PRESIDENTIAL)
- `RoomStatus status`: Current status (AVAILABLE, OCCUPIED, UNDER_MAINTENANCE, RESERVED)
- `int floor`: Floor number where room is located
- `boolean hasWifi`: Whether room has WiFi connectivity
- `boolean hasAC`: Whether room has air conditioning

**Constructor**:
- `Room(String roomNumber, RoomType type, int floor, boolean hasWifi, boolean hasAC)`
  - Initializes room with all properties
  - Status is automatically initialized to AVAILABLE
  - No validation at construction time

**Key Methods**:
- `String getId()`: Returns roomNumber (Manageable implementation)
- `String getRoomNumber()`: Returns the room number/identifier
- `RoomType getType()` / `setType(RoomType)`: Access to room type
- `RoomStatus getStatus()` / `setStatus(RoomStatus)`: Access to room status
- `int getFloor()` / `setFloor(int)`: Access to floor number
- `boolean hasWifi()` / `setHasWifi(boolean)`: WiFi availability
- `boolean hasAC()` / `setHasAC(boolean)`: AC availability
- `double getPricePerNight()`: Returns current nightly rate (from RoomType.basePrice)
- `boolean isAvailable()`: Returns true if status == AVAILABLE
- `@Override String getSummary()`:
  - Format: "ROOM | No: ... | Type: ... | Floor: ... | Status: ... | $/night | WiFi: | AC: "
  - Used for listing rooms and displaying room details
- `@Override boolean validate()`: 
  - Validates roomNumber is not blank
  - Validates type is not null
  - Validates floor >= 0
- `String toFileString()`: Serializes to CSV
  - Format: roomNumber,type,status,floor,hasWifi,hasAC
- `static Room fromFileString(String line)`: Deserializes from CSV line
- `@Override String toString()`: Returns getSummary()

**Business Logic**:
- Tracks availability using status enum
- Price varies by room type
- Amenities (WiFi, AC) are stored as boolean flags
- Can be queried for availability before booking

---

### 8. Booking (Implements Manageable)
**Purpose**: Represents a guest's reservation of a room for specific dates

**Core Fields**:
- `String bookingId`: Unique booking identifier (e.g., "BK001")
- `String guestId`: Reference to the guest (links to Guest.id)
- `String roomNumber`: Reference to the room (links to Room.roomNumber)
- `LocalDate checkIn`: Arrival date
- `LocalDate checkOut`: Departure date
- `boolean isPaid`: Whether the booking has been paid
- `String notes`: Additional booking notes

**Charges & Extras Fields**:
- `double foodCharges`: Total food charges for the booking
- `double gymCharges`: Total gym facility charges
- `double spaCharges`: Total spa facility charges

**Food Breakdown Fields** (with quantities):
- `double foodTibs`, `int foodTibsQty`: Ethiopian dish charges and quantity
- `double foodDorowet`, `int foodDorowetQty`: Another food item charges and quantity
- `double foodBfe`, `int foodBfeQty`: Another food item charges and quantity
- `double foodBreakfastMeals`, `int foodBreakfastMealsQty`: Breakfast charges and quantity
- `double foodFruits`, `int foodFruitsQty`: Fruit charges and quantity

**Beverage Breakdown Fields** (with quantities):
- `double bevWine`, `int bevWineQty`: Wine charges and quantity
- `double bevAlcoholic`, `int bevAlcoholicQty`: Alcoholic beverage charges and quantity
- `double bevWater`, `int bevWaterQty`: Water charges and quantity
- `double bevJuice`, `int bevJuiceQty`: Juice charges and quantity
- `double bevNonAlcoholic`, `int bevNonAlcoholicQty`: Non-alcoholic beverage charges and quantity

**Additional Services**:
- `int gymSessions`: Number of gym sessions used
- `int spaSessions`: Number of spa sessions used

**Constructor**:
- `Booking(String bookingId, String guestId, String roomNumber, LocalDate checkIn, LocalDate checkOut)`
  - Initializes core booking information
  - All charges initialized to 0.0
  - All quantities initialized to 0
  - isPaid initialized to false
  - notes initialized to empty string

**Key Methods**:
- `String getId()`: Returns bookingId (Manageable implementation)
- `String getBookingId()`: Returns the booking identifier
- `String getGuestId()` / `setGuestId(String)`: Guest reference
- `String getRoomNumber()` / `setRoomNumber(String)`: Room reference
- `LocalDate getCheckIn()` / `setCheckIn(LocalDate)`: Arrival date
- `LocalDate getCheckOut()` / `setCheckOut(LocalDate)`: Departure date
- `boolean isPaid()` / `setPaid(boolean)`: Payment status
- `String getNotes()` / `setNotes(String)`: Booking notes
- Getters/setters for all food/beverage charges and quantities
- `double getGymCharges()` / `setGymCharges(double)`: Gym facility charges
- `double getSpaCharges()` / `setSpaCharges(double)`: Spa facility charges
- `int getGymSessions()` / `setGymSessions(int)`: Number of gym sessions
- `int getSpaSessions()` / `setSpaSessions(int)`: Number of spa sessions

**Design Notes**:
- All setter methods use Math.max(0, value) to prevent negative charges/quantities
- Tracks detailed item-level breakdown of food and beverage
- Supports tracking of ancillary services (gym, spa)

---

## SERVICE LAYER DOCUMENTATION

### 1. HotelService
**Purpose**: Central service orchestrating all business logic

**Repository Fields** (Generic pattern):
- `Repository<Guest> guestRepo`: In-memory repository of guests
- `Repository<Room> roomRepo`: In-memory repository of rooms
- `Repository<Booking> bookingRepo`: In-memory repository of bookings
- `Repository<Staff> staffRepo`: In-memory repository of staff

**Supporting Fields**:
- `List<String> activityLog`: Activity history for auditing
- `int bookingCounter`: Auto-increment counter for booking IDs
- `int guestCounter`: Auto-increment counter for guest IDs
- `int staffCounter`: Auto-increment counter for staff IDs
- `double LOYALTY_DISCOUNT`: 15% discount for loyal guests
- `PriceList priceList`: Menu pricing information

**Initialization Methods**:
- `void loadAll()`: Loads all data from files
  - Clears all repositories
  - Loads guests, rooms, bookings, staff from files
  - Updates ID counters based on loaded data
  - Loads activity log history
  
- `void saveAll()`: Saves all data to files
  - Persists guests, rooms, bookings, staff to persistent storage

**Price Management**:
- `double getPrice(String key)`: Gets price for menu item
- `void setPrice(String key, double price)`: Sets price and logs activity
- `Map<String, Double> getAllPrices()`: Returns all menu prices
- `void recordActivity(String event)`: Records action to activity log

---

## UTILITY LAYER DOCUMENTATION

### 1. Repository<T> (Generic Class)
**Purpose**: Generic in-memory CRUD repository for any Manageable entity

**Type Parameter**:
- `<T extends Manageable>`: Only entities implementing Manageable can be stored

**Field**:
- `Map<String, T> map`: LinkedHashMap storing entities by ID (maintains insertion order)

**Methods**:
- `void add(T item)`: Adds item to repository (ID as key)
- `List<T> getAll()`: Returns all items as a List
- `Optional<T> findById(String id)`: Finds item by ID (returns Optional)
- `List<T> search(String keyword)`: Searches items by keyword in getSummary()
  - Returns all items if keyword is null/blank
  - Case-insensitive search
  - Returns items containing keyword in their summary
  
- `boolean update(String id, T item)`: Updates existing item
  - Returns false if item doesn't exist
  - Returns true if update succeeds
  
- `boolean removeById(String id)`: Removes item by ID
  - Returns true if item was removed, false if not found
  
- `void clear()`: Clears all items from repository
- `int count()`: Returns number of items in repository

**Design Pattern**: Generic Repository Pattern
- Provides consistent interface for all entity types
- Type-safe: Compile-time checking prevents type mismatches
- Reusable: Single implementation serves all Manageable entities

---

### 2. FileManager
**Purpose**: Handles persistent file storage for all entities

**File Paths** (in `data/` directory):
- `GUESTS = data/guests.txt`: Stores guest data (CSV format)
- `ROOMS = data/rooms.txt`: Stores room data (CSV format)
- `BOOKINGS = data/bookings.txt`: Stores booking data (CSV format)
- `STAFF = data/staff.txt`: Stores staff data (CSV format)
- `ACTIVITY_LOG = data/activity.log`: Audit trail of system activities

**Key Methods**:
- `static void initialize()`: Creates data directory and files if they don't exist
- `static void appendActivityLog(String entry)`: Appends entry to activity log
- `static List<Guest> loadGuests()`: Loads all guests from file (deserializes from CSV)
- `static List<Room> loadRooms()`: Loads all rooms from file (deserializes from CSV)
- `static List<Booking> loadBookings()`: Loads all bookings from file (deserializes from CSV)
- `static List<Staff> loadStaff()`: Loads all staff members from file (deserializes from CSV)
- `static List<String> loadActivityLog()`: Loads activity log entries
- `static void saveGuests(List<Guest> guests)`: Saves guests to file (serializes to CSV)
- `static void saveRooms(List<Room> rooms)`: Saves rooms to file (serializes to CSV)
- `static void saveBookings(List<Booking> bookings)`: Saves bookings to file (serializes to CSV)
- `static void saveStaff(List<Staff> staff)`: Saves staff to file (serializes to CSV)

**Serialization Format**: CSV (Comma-Separated Values)
- Each entity uses toFileString() for serialization
- Each entity has static fromFileString() for deserialization
- Lost of precision in some floating-point values

---

## EXCEPTION LAYER DOCUMENTATION

### 1. HotelException
**Purpose**: Base custom exception for all hotel-related errors

**Constructors**:
- `HotelException()`: No-arg constructor
- `HotelException(String message)`: Constructor with error message
- `HotelException(String message, Throwable cause)`: Constructor with message and cause
- `HotelException(Throwable cause)`: Constructor with cause only

**Usage**:
- Base exception for all hotel operations
- Subclassed by EntityNotFoundException and RoomNotAvailableException
- Used in file I/O, validation, and business logic

---

### 2. EntityNotFoundException
**Purpose**: Thrown when searching for non-existent entity

**Usage Scenarios**:
- Guest not found by ID
- Room not found by number
- Booking not found by ID
- Staff not found by ID

---

### 3. RoomNotAvailableException
**Purpose**: Thrown when attempting to book unavailable room

**Usage Scenarios**:
- Room status is not AVAILABLE
- Dates conflict with existing booking
- Room is under maintenance

---

## KEY DESIGN PATTERNS USED

1. **Inheritance Hierarchy**: Person → Guest, Staff
2. **Interface Implementation**: All major classes implement Manageable
3. **Generic Repository Pattern**: Repository<T extends Manageable>
4. **Enum Pattern**: RoomType and RoomStatus as fixed value sets
5. **Factory Pattern**: fromFileString() static methods for deserialization
6. **Exception Handling**: Custom exception hierarchy for domain-specific errors
7. **Encapsulation**: Private fields with public accessors
8. **Polymorphism**: Abstract methods like getRole() overridden in subclasses
9. **Abstraction**: Abstract Person class and Manageable interface
10. **Singleton Pattern**: HotelService as central coordinator (implicit)

---

## FIELD VALIDATION RULES

**Person Fields**:
- id: NOT NULL, NOT BLANK
- firstName: NOT NULL, NOT BLANK
- lastName: NOT NULL, NOT BLANK
- phone: NOT NULL, NOT BLANK
- email: (usually NOT NULL, NOT BLANK for guests)

**Guest Additional**:
- idNumber: NOT NULL, NOT BLANK (required for guest validity)

**Room Fields**:
- roomNumber: NOT NULL, NOT BLANK
- type: NOT NULL
- floor: >= 0

**Booking Fields**:
- bookingId: NOT NULL
- guestId: Reference to existing guest
- roomNumber: Reference to existing room
- checkIn: Must be before checkOut
- checkOut: Must be after checkIn

---

## CLASS RELATIONSHIPS

```
Manageable (Interface)
    ├── Person (Abstract)
    │   ├── Guest
    │   └── Staff
    ├── Room
    └── Booking

Repository<T extends Manageable>
    ├── Repository<Guest>
    ├── Repository<Room>
    ├── Repository<Booking>
    └── Repository<Staff>

HotelService (contains all repositories)

FileManager (persists data)

RoomType (Enum) ← used by Room
RoomStatus (Enum) ← used by Room

HotelException (Base Exception)
    ├── EntityNotFoundException
    └── RoomNotAvailableException
```

