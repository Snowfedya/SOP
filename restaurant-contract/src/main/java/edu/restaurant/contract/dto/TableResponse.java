package edu.restaurant.contract.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO для ответа со столиком
 */
public record TableResponse(
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("tableNumber")
        String tableNumber,
        
        @JsonProperty("capacity")
        Integer capacity,
        
        @JsonProperty("location")
        String location,
        
        @JsonProperty("status")
        TableStatus status
) {
    public Long getId() {
        return id;
    }
    
    public String getTableNumber() {
        return tableNumber;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public String getLocation() {
        return location;
    }
    
    public TableStatus getStatus() {
        return status;
    }
}
