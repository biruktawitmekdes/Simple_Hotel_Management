package exception;

public class RoomNotAvailableException extends HotelException {
    public RoomNotAvailableException(String roomNumber) {
        super("Room " + roomNumber + " is not available.");
    }
}
