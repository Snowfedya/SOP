package edu.restaurant.contract.exception;

/**
 * Исключение для случая, когда ресурс не найден
 */
public class ResourceNotFoundException extends RuntimeException {
    
    private final String resourceName;
    private final Object resourceId;

    public ResourceNotFoundException(String message, String resourceName, Object resourceId) {
        super(message);
        this.resourceName = resourceName;
        this.resourceId = resourceId;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.resourceId = null;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Object getResourceId() {
        return resourceId;
    }
}
