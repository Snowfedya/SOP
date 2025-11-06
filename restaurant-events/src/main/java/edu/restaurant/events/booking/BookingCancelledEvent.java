package edu.restaurant.events.booking;

import edu.restaurant.events.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCancelledEvent implements DomainEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private Long bookingId;
    private String reason;
    private LocalDateTime cancelledAt;

    @Override
    public String getEventType() { return "BOOKING_CANCELLED"; }
}
