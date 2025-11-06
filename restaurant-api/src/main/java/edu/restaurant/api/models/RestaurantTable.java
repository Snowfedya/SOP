package edu.restaurant.api.models;

import edu.restaurant.contract.dto.TableStatus;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tables")
@Data
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tableNumber;

    @Column(nullable = false)
    private int capacity;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status;
}
