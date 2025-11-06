package edu.restaurant.events.table;

import edu.restaurant.events.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableStatusChangedEvent implements DomainEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private Long tableId;
    private String oldStatus;
    private String newStatus;

    @Override
    public String getEventType() { return "TABLE_STATUS_CHANGED"; }
}
