package edu.restaurant.events;

import java.io.Serializable;
import java.time.LocalDateTime;

public interface DomainEvent extends Serializable {
    String getEventId();
    LocalDateTime getOccurredAt();
    String getEventType();
}
