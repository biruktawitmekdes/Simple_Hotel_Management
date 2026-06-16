package exception;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String entity, String id) {
        super(entity + " not found: " + id);
    }
}
