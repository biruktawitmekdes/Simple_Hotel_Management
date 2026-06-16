# Hotel Management System - Complete Class Reference Guide

## PROJECT SUMMARY

This is a comprehensive Hotel Management System built in Java demonstrating enterprise-level Object-Oriented Programming principles. The system manages guests, staff, rooms, and bookings with full CRUD operations, persistence, and business logic.

**Project Structure:**
- **Model Layer** (`src/model/`): Core domain entities
- **Service Layer** (`src/service/`): Business logic orchestration
- **Utility Layer** (`src/util/`): Generic repositories and file management
- **Exception Layer** (`src/exception/`): Custom exception hierarchy
- **UI Layer** (`src/ui/`): Console-based user interface

---

## COMPLETE CLASS REFERENCE

### INTERFACE: Manageable
**Package**: `model`
**Purpose**: Contract for all manageable entities

**Methods**:
1. `String getId()`
   - Returns unique entity identifier
   - Used as repository key
   - Examples: "G001", "102", "BK2024001"

2. `String getSummary()`
   - Returns formatted display string
   - Used in console output and listings
   - Columnar/CSV-like format for readability

3. `boolean validate()`
   - Validates entity's required fields
   - Called before persistence
   - Each implementation has specific rules

**Implementations**: Person (abstract), Guest, Staff, Room, Booking

---

### ABSTRACT CLASS: Person
**Package**: `model`
**Implements**: Manageable
**Purpose**: Base class for all persons (Guests and Staff)

**FIELDS**:
- `String id` - Unique identifier (e.g., "G001", "S001")
- `String firstName` - Person's first name (required)
- `String lastName` - Person's last name (required)
- `String phone` - Contact phone (required)
- `String email` - Contact email (required)

**CONSTRUCTOR**:
```java
Person(String id, String firstName, String lastName, String phone, String email)
```
Initializes all base person fields. No validation at construction time.

**METHODS**:

**Accessors**:
- `String getId()` - Returns person's unique ID
- `String getFirstName()` / `setFirstName(String)` - First name access
- `String getLastName()` / `setLastName(String)` - Last name access
- `String getFullName()` - Returns "FirstName LastName"
- `String getPhone()` / `setPhone(String)` - Phone access
- `String getEmail()` / `setEmail(String)` - Email access

**Validation & Interface**:
- `@Override boolean validate()` - Validates id, firstName, lastName, phone are non-blank
- `abstract String getRole()` - POLYMORPHIC: Subclasses return "Guest" or "Staff"
- `@Override String toString()` - Returns "[ID] FullName | Phone: ... | Email: ..."

**Subclasses**: Guest, Staff

---

### CONCRETE CLASS: Guest
**Package**: `model`
**Extends**: Person
**Purpose**: Represents a hotel guest

**ADDITIONAL FIELDS**:
- `String nationality` - Country of origin (e.g., "Ethiopia", "USA")
- `String idNumber` - National ID or Passport (required for validation)
- `int totalStays` - Number of previous stays (starts at 0)

**CONSTRUCTOR**:
```java
Guest(String id, String firstName, String lastName, String phone, String email, String nationality, String idNumber)
```

**ADDITIONAL METHODS**:
- `String getNationality()` / `setNationality(String)` - Nationality access
- `String getIdNumber()` / `setIdNumber(String)` - ID/Passport access
- `int getTotalStays()` - Returns stay count
- `void incrementStays()` - Increments stay count (called on checkout)
- `@Override String getRole()` - Returns "Guest"
- `@Override String getSummary()` - Formatted display with nationality
- `@Override boolean validate()` - Extends parent validation + requires idNumber
- `String toFileString()` - CSV serialization
- `static Guest fromFileString(String line)` - CSV deserialization

---

### CONCRETE CLASS: Staff
**Package**: `model`
**Extends**: Person
**Purpose**: Represents a hotel staff member

**ADDITIONAL FIELDS**:
- `String department` - Department (e.g., "Housekeeping", "Front Desk")
- `String position` - Job title (e.g., "Manager", "Attendant")
- `double salary` - Monthly/annual salary

**CONSTRUCTOR**:
```java
Staff(String id, String firstName, String lastName, String phone, String email, String department, String position, double salary)
```

**ADDITIONAL METHODS**:
- `String getDepartment()` / `setDepartment(String)` - Department access
- `String getPosition()` / `setPosition(String)` - Position access
- `double getSalary()` / `setSalary(double)` - Salary access
- `@Override String getRole()` - Returns "Staff"
- `@Override String getSummary()` - Formatted display with department and position
- `String toFileString()` - CSV serialization
- `static Staff fromFileString(String line)` - CSV deserialization

---

### ENUM: RoomType
**Package**: `model`
**Purpose**: Defines room types with pricing

**CONSTANTS**:
- `SINGLE("Single", 50.0)` - Single bed, $50/night
- `DOUBLE("Double", 80.0)` - Double bed, $80/night
- `SUITE("Suite", 150.0)` - Suite with living area, $150/night
- `DELUXE("Deluxe", 200.0)` - Premium room, $200/night
- `PRESIDENTIAL("Presidential", 500.0)` - Luxury suite, $500/night

**METHODS**:
- `String getDisplayName()` - Returns display name (e.g., "Single", "Double")
- `double getBasePrice()` - Returns base nightly rate
- `@Override String toString()` - Returns displayName

**FIELDS** (per constant):
- `String displayName` - Human-readable name
- `double basePrice` - Base price per night

---

### ENUM: RoomStatus
**Package**: `model`
**Purpose**: Defines room availability states

**CONSTANTS**:
- `AVAILABLE("Available")` - Ready for booking
- `OCCUPIED("Occupied")` - Currently occupied by guest
- `RESERVED("Reserved")` - Reserved for future booking
- `UNDER_MAINTENANCE("Under Maintenance")` - In maintenance, unavailable

**METHODS**:
- `String getDisplayName()` - Returns display name
- `@Override String toString()` - Returns displayName

**State Transitions**:
- AVAILABLE → OCCUPIED (check-in)
- OCCUPIED → AVAILABLE (check-out)
- AVAILABLE → RESERVED (booking)
- RESERVED → OCCUPIED (check-in for reservation)
- Any → UNDER_MAINTENANCE (maintenance)
- UNDER_MAINTENANCE → AVAILABLE (maintenance complete)

---

### CONCRETE CLASS: Room
**Package**: `model`
**Implements**: Manageable
**Purpose**: Represents a physical hotel room

**FIELDS**:
- `String roomNumber` - Unique room identifier (e.g., "101", "202")
- `RoomType type` - Type of room (SINGLE, DOUBLE, SUITE, DELUXE, PRESIDENTIAL)
- `RoomStatus status` - Current status (AVAILABLE, OCCUPIED, RESERVED, UNDER_MAINTENANCE)
- `int floor` - Floor number (1-based)
- `boolean hasWifi` - WiFi connectivity flag
- `boolean hasAC` - Air conditioning flag

**CONSTRUCTOR**:
```java
Room(String roomNumber, RoomType type, int floor, boolean hasWifi, boolean hasAC)
```
Status is automatically initialized to AVAILABLE.

**METHODS**:

**Accessors**:
- `String getId()` - Returns roomNumber (Manageable implementation)
- `String getRoomNumber()` - Returns room number
- `RoomType getType()` / `setType(RoomType)` - Room type access
- `RoomStatus getStatus()` / `setStatus(RoomStatus)` - Status access
- `int getFloor()` / `setFloor(int)` - Floor access
- `boolean hasWifi()` / `setHasWifi(boolean)` - WiFi flag
- `boolean hasAC()` / `setHasAC(boolean)` - AC flag
- `double getPricePerNight()` - Returns room type's base price

**Business Logic**:
- `boolean isAvailable()` - Returns true if status == AVAILABLE
- `@Override String getSummary()` - Formatted columnar display with all details
- `@Override boolean validate()` - Validates roomNumber, type, floor >= 0
- `String toFileString()` - CSV serialization
- `static Room fromFileString(String line)` - CSV deserialization
- `@Override String toString()` - Calls getSummary()

---

### CONCRETE CLASS: Booking
**Package**: `model`
**Implements**: Manageable
**Purpose**: Represents a guest's room reservation

**CORE FIELDS**:
- `String bookingId` - Unique booking identifier (e.g., "BK001")
- `String guestId` - Reference to guest (foreign key to Guest.id)
- `String roomNumber` - Reference to room (foreign key to Room.roomNumber)
- `LocalDate checkIn` - Arrival date
- `LocalDate checkOut` - Departure date
- `boolean isPaid` - Payment status
- `String notes` - Additional booking notes

**CHARGES FIELDS**:
- `double foodCharges` - Total food charges
- `double gymCharges` - Total gym facility charges
- `double spaCharges` - Total spa facility charges

**DETAILED FOOD BREAKDOWN** (with quantities):
- `double foodTibs`, `int foodTibsQty` - Ethiopian dish pricing and quantity
- `double foodDorowet`, `int foodDorowetQty` - Food item pricing and quantity
- `double foodBfe`, `int foodBfeQty` - Food item pricing and quantity
- `double foodBreakfastMeals`, `int foodBreakfastMealsQty` - Breakfast pricing and quantity
- `double foodFruits`, `int foodFruitsQty` - Fruit pricing and quantity

**DETAILED BEVERAGE BREAKDOWN** (with quantities):
- `double bevWine`, `int bevWineQty` - Wine pricing and quantity
- `double bevAlcoholic`, `int bevAlcoholicQty` - Alcoholic beverage pricing and quantity
- `double bevWater`, `int bevWaterQty` - Water pricing and quantity
- `double bevJuice`, `int bevJuiceQty` - Juice pricing and quantity
- `double bevNonAlcoholic`, `int bevNonAlcoholicQty` - Non-alcoholic beverage pricing and quantity

**SERVICES**:
- `int gymSessions` - Number of gym sessions used
- `int spaSessions` - Number of spa sessions used

**CONSTRUCTOR**:
```java
Booking(String bookingId, String guestId, String roomNumber, LocalDate checkIn, LocalDate checkOut)
```
All charges and quantities initialized to 0/0.0, isPaid=false.

**METHODS** (All have validation using Math.max(0, value)):
- Getter/setter methods for all fields
- `String getId()` - Returns bookingId (Manageable implementation)
- Setters prevent negative values using Math.max(0, value)

**Notes**: 
- Tracks detailed breakdown of all charges
- All setters validate non-negative values
- Supports complex billing scenarios with itemized charges

---

## SERVICE LAYER

### HotelService
**Package**: `service`
**Purpose**: Central orchestrator for all business logic

**REPOSITORIES** (Generic pattern):
- `Repository<Guest> guestRepo` - Manages guests
- `Repository<Room> roomRepo` - Manages rooms
- `Repository<Booking> bookingRepo` - Manages bookings
- `Repository<Staff> staffRepo` - Manages staff

**SUPPORTING FIELDS**:
- `List<String> activityLog` - Audit trail
- `int bookingCounter`, `int guestCounter`, `int staffCounter` - Auto-increment IDs
- `double LOYALTY_DISCOUNT = 0.15` - 15% loyalty discount
- `PriceList priceList` - Menu pricing

**KEY METHODS**:
- `void loadAll()` - Loads all data from files
- `void saveAll()` - Saves all data to files
- `void recordActivity(String event)` - Logs system activities
- Booking, guest, room, and staff management methods
- Price management methods

---

## UTILITY LAYER

### Repository<T> (Generic Class)
**Package**: `util`
**Type Parameter**: `<T extends Manageable>`
**Purpose**: Generic in-memory CRUD repository

**FIELD**:
- `Map<String, T> map` - LinkedHashMap for storage and ordering

**METHODS**:
- `void add(T item)` - Adds/updates entity
- `List<T> getAll()` - Returns all entities
- `Optional<T> findById(String id)` - Finds by ID
- `List<T> search(String keyword)` - Searches by keyword
- `boolean update(String id, T item)` - Updates existing entity
- `boolean removeById(String id)` - Removes by ID
- `void clear()` - Clears all entities
- `int count()` - Returns entity count

**Usage**:
```java
Repository<Guest> guestRepo = new Repository<>();
guestRepo.add(guest);
Optional<Guest> found = guestRepo.findById("G001");
List<Guest> all = guestRepo.getAll();
```

---

### FileManager
**Package**: `util`
**Purpose**: Handles file persistence

**FILE PATHS** (in `data/` directory):
- `data/guests.txt` - Guest data (CSV)
- `data/rooms.txt` - Room data (CSV)
- `data/bookings.txt` - Booking data (CSV)
- `data/staff.txt` - Staff data (CSV)
- `data/activity.log` - Activity log

**KEY METHODS**:
- `static void initialize()` - Creates data directory and files
- `static List<Guest> loadGuests()` - Loads guests from file
- `static List<Room> loadRooms()` - Loads rooms from file
- `static List<Booking> loadBookings()` - Loads bookings from file
- `static List<Staff> loadStaff()` - Loads staff from file
- `static void saveGuests(List<Guest> guests)` - Saves guests to file
- `static void saveRooms(List<Room> rooms)` - Saves rooms to file
- `static void saveBookings(List<Booking> bookings)` - Saves bookings to file
- `static void saveStaff(List<Staff> staff)` - Saves staff to file

---

## EXCEPTION LAYER

### HotelException
**Package**: `exception`
**Extends**: Exception
**Purpose**: Base custom exception for hotel operations

**CONSTRUCTORS**:
- `HotelException()` - Default constructor
- `HotelException(String message)` - With message
- `HotelException(String message, Throwable cause)` - With message and cause
- `HotelException(Throwable cause)` - With cause

**Subclasses**: EntityNotFoundException, RoomNotAvailableException

---

### EntityNotFoundException
**Package**: `exception`
**Extends**: HotelException
**Purpose**: Thrown when entity not found

**Used when**:
- Guest not found by ID
- Room not found by number
- Booking not found by ID
- Staff not found by ID

---

### RoomNotAvailableException
**Package**: `exception`
**Extends**: HotelException
**Purpose**: Thrown when room cannot be booked

**Used when**:
- Room status is not AVAILABLE
- Date conflict with existing booking
- Room is under maintenance

---

## KEY DESIGN PATTERNS

1. **Inheritance Hierarchy**: Person → (Guest, Staff)
2. **Interface Implementation**: Multiple classes implement Manageable
3. **Generic Repository Pattern**: Repository<T extends Manageable>
4. **Enum Pattern**: RoomType, RoomStatus
5. **Factory Pattern**: fromFileString() static methods
6. **Exception Hierarchy**: HotelException with specific subclasses
7. **Encapsulation**: Private fields with public accessors
8. **Polymorphism**: Abstract methods overridden in subclasses
9. **Abstraction**: Abstract Person class
10. **Template Method**: HotelService orchestrating operations

---

## OOP PRINCIPLES DEMONSTRATED

- **ENCAPSULATION**: Private fields with controlled getter/setter access
- **INHERITANCE**: Guest and Staff inherit from Person
- **ABSTRACTION**: Abstract Person class and Manageable interface
- **POLYMORPHISM**: Overridden methods like getRole(), getSummary()
- **COMPOSITION**: HotelService contains Repository instances
- **GENERICS**: Repository<T> provides type-safe collections
- **DEPENDENCY INJECTION**: Services accept repositories

---

## FILE STRUCTURE

```
hotel_management/
├── src/
│   ├── Main.java (Entry point)
│   ├── model/
│   │   ├── Manageable.java (Interface)
│   │   ├── Person.java (Abstract)
│   │   ├── Guest.java
│   │   ├── Staff.java
│   │   ├── Room.java
│   │   ├── Booking.java
│   │   ├── RoomType.java (Enum)
│   │   └── RoomStatus.java (Enum)
│   ├── service/
│   │   ├── HotelService.java
│   │   └── PriceList.java
│   ├── util/
│   │   ├── Repository.java (Generic)
│   │   └── FileManager.java
│   ├── exception/
│   │   ├── HotelException.java
│   │   ├── EntityNotFoundException.java
│   │   └── RoomNotAvailableException.java
│   └── ui/
│       └── ConsoleUI.java
├── data/
│   ├── guests.txt
│   ├── rooms.txt
│   ├── bookings.txt
│   ├── staff.txt
│   └── activity.log
└── bin/ (Compiled classes)
```

---

## VALIDATION RULES

**Person Fields** (All required, non-blank):
- id, firstName, lastName, phone

**Guest Additional**:
- idNumber (required, non-blank)

**Room Fields**:
- roomNumber (required, non-blank)
- type (required, not null)
- floor (>= 0)

**Booking**:
- References valid Guest and Room
- checkIn must be before checkOut
- All charges >= 0

---

## Data Flow

1. **Initialization**: FileManager.initialize() creates data directory
2. **Load**: HotelService.loadAll() reads from files via FileManager
3. **Runtime**: Changes stored in Repository instances in memory
4. **Persistence**: HotelService.saveAll() writes to files via FileManager
5. **Serialization**: toFileString() converts objects to CSV
6. **Deserialization**: fromFileString() reconstructs objects from CSV

---

## Complete Method Index

See DETAILED_CLASS_DOCUMENTATION.md for comprehensive field-by-field documentation.

