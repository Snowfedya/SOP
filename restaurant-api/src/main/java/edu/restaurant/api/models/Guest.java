package edu.restaurant.api.models;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Guest {
    private String guestName;
    private String phoneNumber;
    private String email;
}
