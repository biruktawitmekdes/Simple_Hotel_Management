# UML Diagram Generation Prompt for Hotel Management System

## Project Overview
Generate a comprehensive UML class diagram for a **Hotel Management System** built in Java that demonstrates OOP principles (inheritance, polymorphism, abstraction, encapsulation, interfaces, generics).

## Core Classes and Relationships

### 1. Interface Layer
- **Manageable (Interface)**
  - Methods: `getId()`, `getSummary()`, `validate()`
  - Implemented by: Person, Room, Booking

### 2. Enumeration Types
- **RoomType (Enum)**
  - Values: SINGLE, DOUBLE, SUITE, DELUXE, PRESIDENTIAL
  - Each has: displayName (String), basePrice (double)
  
- **RoomStatus (Enum)**
  - Values: AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED

### 3. Abstract Class (Inheritance Hierarchy)
- **Person (Abstract Class - implements Manageable)**
  - Fields: id, firstName, lastName, phone, email
  - Methods: getId(), getFullName(), validate(), getSummary() (abstract)
  - Inheritance Relations:
    - **Guest (extends Person)**
      - Additional Fields: nationality, idNumber, totalStays
      - Override: getRole(), getSummary()
    - **Staff (extends Person)**
      - Additional Fields: department, position, salary
      - Override: getRole(), getSummary()

### 4. Model Classes (Concrete)
- **Room (implements Manageable)**
  - Fields: roomNumber, type (RoomType), status (RoomStatus), floor, hasWifi, hasAC
  - Methods: getId(), getStatus(), setStatus(), isAvailable(), getPricePerNight(), validate(), getSummary()
  - Relationships: Uses RoomType enum, Uses RoomStatus enum

- **Booking (implements Manageable)**
  - Fields: bookingId, guestId, roomNumber, checkIn (LocalDate), checkOut (LocalDate), isPaid
  - Additional Fields: foodCharges, gymCharges, spaCharges, notes
  - Methods: getId(), validate(), getSummary(), calculate total cost methods
  - Associations: 
    - References Guest (via guestId)
    - References Room (via roomNumber)

### 5. Service Layer
- **HotelService**
  - Contains:
    - Repository<Guest> guestRepo
    - Repository<Room> roomRepo
    - Repository<Booking> bookingRepo
    - Repository<Staff> staffRepo
    - List<String> activityLog
  - Main business logic methods: bookRoom(), cancelBooking(), searchGuests(), calculateBill(), etc.

### 6. Utility/Generic Classes
- **Repository<T> (Generic class - where T extends Manageable)**
  - Fields: Map<String, T> map
  - Methods: add(T), getAll(), findById(String), update(String, T), removeById(String), search(String), clear(), count()
  - Used by: HotelService with Guest, Room, Booking, Staff types

- **FileManager**
  - Methods for: saving/loading guest data, room data, booking data, staff data from text files

### 7. Exception Layer (Custom Exceptions)
- **HotelException (extends Exception)**
  - Base custom exception class
  
- **EntityNotFoundException (extends HotelException)**
  - Thrown when Guest, Room, Staff, or Booking not found
  
- **RoomNotAvailableException (extends HotelException)**
  - Thrown when attempting to book unavailable rooms

## Key Relationships to Show in UML:

1. **Inheritance**: Guest → Person, Staff → Person
2. **Interface Implementation**: Person → Manageable, Room → Manageable, Booking → Manageable
3. **Composition/Aggregation**: 
   - HotelService contains Repository<T> instances for each entity
   - Booking references Guest and Room
4. **Dependency**:
   - Room uses RoomType enum and RoomStatus enum
   - HotelService uses Repository, FileManager, and custom exceptions
5. **Generic Relationships**: Repository<T extends Manageable>

## Diagram Style Requirements:
- Use standard UML notation with proper symbols:
  - Abstract classes with italics or special notation
  - Interfaces with proper interface symbol (or <<interface>> notation)
  - Inheritance arrows (solid line with hollow arrowhead)
  - Interface implementation arrows (dashed line with hollow arrowhead)
  - Associations/Dependencies (solid lines with labels where needed)
  - Enumerations with special notation
  - Generic types with type parameters shown
- Include visibility modifiers: + (public), - (private), # (protected)
- Include method signatures and key attributes
- Organize classes into logical packages: model, service, util, exception

## Additional Notes:
- The system demonstrates polymorphism through Person subclasses (Guest and Staff)
- The Repository pattern is used with generics for data persistence
- The Manageable interface ensures all storable entities implement standard CRUD operations
- Exception hierarchy provides custom error handling specific to hotel operations

---

**Generate a clear, professional UML class diagram based on these specifications.**
