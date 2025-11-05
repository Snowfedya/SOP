package edu.restaurant.events;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Base interface for all domain events.
 */
public interface DomainEvent extends Serializable {

    /**
     * @return The unique identifier of the event.
     */
    default UUID eventId() {
        return UUID.randomUUID();
    }

    /**
     * @return The timestamp when the event occurred.
     */
    default Instant occurredAt() {
        return Instant.now();
    }

    /**
     * @return The type of the event, typically the simple class name.
     */
    default String eventType() {
        return this.getClass().getSimpleName();
    }
}
