package edu.restaurant.events;

import java.io.Serializable;

public record BookingDeletedEvent(Long bookingId) implements Serializable {
}
