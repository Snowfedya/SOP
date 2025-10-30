package edu.restaurant.contract.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * DTO для ответа с бронированием
 */
public record BookingResponse(
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("guestName")
        String guestName,
        
        @JsonProperty("phoneNumber")
        String phoneNumber,
        
        @JsonProperty("email")
        String email,
        
        @JsonProperty("tableId")
        Long tableId,
        
        @JsonProperty("bookingDateTime")
        LocalDateTime bookingDateTime,
        
        @JsonProperty("numberOfGuests")
        Integer numberOfGuests,
        
        @JsonProperty("specialRequests")
        String specialRequests,
        
        @JsonProperty("status")
        BookingStatus status,
        
        @JsonProperty("createdAt")
        LocalDateTime createdAt
) {
    public Long getId() {
        return id;
    }
    
    public String getGuestName() {
        return guestName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public Long getTableId() {
        return tableId;
    }
    
    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }
    
    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }
    
    public String getSpecialRequests() {
        return specialRequests;
    }
    
    public BookingStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
