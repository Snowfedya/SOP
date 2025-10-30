package edu.restaurant.contract.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO для создания/обновления столика
 */
public record TableRequest(
        
        @NotBlank(message = "Номер столика обязателен")
        @Size(min = 1, max = 10, message = "Номер столика должен быть от 1 до 10 символов")
        String tableNumber,
        
        @NotNull(message = "Количество мест обязательно")
        @Min(value = 1, message = "Количество мест должно быть не менее 1")
        Integer capacity,
        
        @NotBlank(message = "Расположение столика обязательно")
        @Size(min = 2, max = 100, message = "Расположение должно быть от 2 до 100 символов")
        String location,
        
        @NotNull(message = "Статус столика обязателен")
        TableStatus status
) {}
