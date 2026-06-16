package util;

import model.Manageable;

import java.util.*;

/**
 * Generic in-memory repository for managing entities.
 * 
 * The Repository class provides a generic, type-safe CRUD (Create, Read, Update, Delete)
 * implementation for any class that implements the Manageable interface. This class
 * demonstrates the Repository Design Pattern and Generic Programming in Java.
 * 
 * DESIGN PATTERNS:
 * - Repository Pattern: Abstracts data access logic
 * - Generic Programming: Type parameter T ensures compile-time type safety
 * 
 * STORAGE: Uses LinkedHashMap<String, T> to maintain insertion order and O(1) operations
 * 
 * TYPE SAFETY: <T extends Manageable> ensures only Manageable entities can be stored
 * 
 * USAGE: Instantiate with specific entity type:
 * - Repository<Guest> guestRepo = new Repository<>();
 * - Repository<Room> roomRepo = new Repository<>();
 * - Repository<Booking> bookingRepo = new Repository<>();
 * - Repository<Staff> staffRepo = new Repository<>();
 * 
 * @param <T> Generic type parameter - must implement Manageable interface
 * @author Hotel Management System
 * @version 1.0
 */
public class Repository<T extends Manageable> {
    
    /**
     * Internal storage map for entities.
     * Uses LinkedHashMap to:
     * 1. Maintain insertion order (useful for stable iteration)
     * 2. Provide O(1) average case lookup, insertion, deletion
     * 3. Use entity ID (from getId()) as key
     */
    private final Map<String, T> map = new LinkedHashMap<>();

    /**
     * Adds a new entity to the repository.
     * 
     * If an entity with the same ID already exists, it will be replaced (update behavior).
     * This method calls getId() on the entity to determine its storage key.
     * 
     * @param item The entity to add (must not be null)
     * @throws NullPointerException if item is null
     */
    public void add(T item) { map.put(item.getId(), item); }

    /**
     * Retrieves all entities in the repository.
     * 
     * Returns a new ArrayList containing all values to prevent external modification
     * of the internal map. Maintains insertion order due to LinkedHashMap usage.
     * 
     * @return List of all entities in the repository, or empty list if repository is empty
     */
    public List<T> getAll() { return new ArrayList<>(map.values()); }

    /**
     * Finds a single entity by its unique identifier.
     * 
     * Uses Optional to handle the case where an entity might not exist,
     * providing a null-safe way to check for existence and retrieve the entity.
     * 
     * @param id The unique identifier of the entity to find
     * @return Optional containing the entity if found, or empty Optional if not found
     *         USAGE: Optional<Guest> guest = guestRepo.findById("G001");
     *                if (guest.isPresent()) { ... }
     */
    public Optional<T> findById(String id) { return Optional.ofNullable(map.get(id)); }

    /**
     * Searches for entities by keyword in their summary representation.
     * 
     * If keyword is null or blank, returns all entities (no filtering).
     * Search is case-insensitive and checks if the keyword appears anywhere in getSummary().
     * 
     * IMPLEMENTATION: Iterates through all entities and filters by summary content
     * PERFORMANCE: O(n) where n is number of entities (full scan search)
     * 
     * @param keyword Search term (case-insensitive substring match on getSummary())
     * @return List of matching entities, or all entities if keyword is null/blank
     *         EXAMPLES:
     *         - search("John") finds all entities with "john" in summary
     *         - search("102") finds room or booking with "102" in summary
     *         - search("") returns all entities
     */
    public List<T> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAll();
        String k = keyword.toLowerCase();
        List<T> out = new ArrayList<>();
        for (T t : map.values()) if (t.getSummary().toLowerCase().contains(k)) out.add(t);
        return out;
    }

    /**
     * Updates an existing entity by replacing it with new data.
     * 
     * Only updates if an entity with the given ID already exists in the repository.
     * New entity must have the same ID as the old entity.
     * 
     * @param id The ID of the entity to update
     * @param item The new entity data
     * @return true if update was successful (entity existed and was replaced)
     *         false if entity with given ID was not found (no update occurred)
     */
    public boolean update(String id, T item) {
        if (!map.containsKey(id)) return false;
        map.put(id, item);
        return true;
    }

    /**
     * Removes an entity from the repository by its ID.
     * 
     * @param id The ID of the entity to remove
     * @return true if entity was found and removed, false if not found
     */
    public boolean removeById(String id) { return map.remove(id) != null; }

    /**
     * Clears all entities from the repository.
     * Repository will be empty after this operation.
     */
    public void clear() { map.clear(); }

    /**
     * Gets the number of entities currently in the repository.
     * 
     * @return Count of entities (0 if repository is empty)
     */
    public int count() { return map.size(); }
}
