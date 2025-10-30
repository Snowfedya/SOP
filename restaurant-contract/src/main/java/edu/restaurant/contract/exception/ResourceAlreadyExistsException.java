package edu.restaurant.contract.exception;

/**
 * Исключение для случая, когда ресурс уже существует
 */
public class ResourceAlreadyExistsException extends RuntimeException {
    
    private final String resourceName;
    private final Object resourceValue;

    public ResourceAlreadyExistsException(String message, String resourceName, Object resourceValue) {
        super(message);
        this.resourceName = resourceName;
        this.resourceValue = resourceValue;
    }

    public ResourceAlreadyExistsException(String message) {
        super(message);
        this.resourceName = null;
        this.resourceValue = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Object getResourceValue() {
        return resourceValue;
    }
}
