package edu.restaurant.events.booking;

import edu.restaurant.events.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent implements DomainEvent {
    private String eventId;
    private LocalDateTime occurredAt;
    private Long bookingId;
    private String guestName;
    private String phoneNumber;
    private String email;
    private Long tableId;
    private LocalDateTime bookingDateTime;
    private Integer numberOfGuests;

    @Override
    public String getEventType() { return "BOOKING_CREATED"; }
}
