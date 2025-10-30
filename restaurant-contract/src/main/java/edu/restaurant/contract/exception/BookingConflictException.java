package edu.restaurant.contract.exception;

/**
 * Исключение для случая конфликта бронирования
 */
public class BookingConflictException extends RuntimeException {
    
    private final Long tableId;
    private final String dateTime;

    public BookingConflictException(String message, Long tableId, String dateTime) {
        super(message);
        this.tableId = tableId;
        this.dateTime = dateTime;
    }

    public BookingConflictException(String message) {
        super(message);
        this.tableId = null;
        this.dateTime = null;
    }

    public Long getTableId() {
        return tableId;
    }

    public String getDateTime() {
        return dateTime;
    }
}
