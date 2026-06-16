package util;

import model.Manageable;

import java.util.*;

/**
 * Minimal generic in-memory repository used by the demo project.
 */
public class Repository<T extends Manageable> {
    private final Map<String, T> map = new LinkedHashMap<>();

    public void add(T item) { map.put(item.getId(), item); }

    public List<T> getAll() { return new ArrayList<>(map.values()); }

    public Optional<T> findById(String id) { return Optional.ofNullable(map.get(id)); }

    public List<T> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return getAll();
        String k = keyword.toLowerCase();
        List<T> out = new ArrayList<>();
        for (T t : map.values()) if (t.getSummary().toLowerCase().contains(k)) out.add(t);
        return out;
    }

    public boolean update(String id, T item) {
        if (!map.containsKey(id)) return false;
        map.put(id, item);
        return true;
    }

    public boolean removeById(String id) { return map.remove(id) != null; }

    public void clear() { map.clear(); }

    public int count() { return map.size(); }
}
