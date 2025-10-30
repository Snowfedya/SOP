package edu.restaurant.contract.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * DTO для создания/обновления бронирования
 */
public record BookingRequest(
        
        @NotBlank(message = "Имя гостя обязательно")
        @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
        String guestName,
        
        @NotBlank(message = "Телефон обязателен")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Неверный формат телефона")
        String phoneNumber,
        
        @Email(message = "Неверный формат email")
        String email,
        
        @NotNull(message = "ID столика обязателен")
        Long tableId,
        
        @NotNull(message = "Дата и время бронирования обязательны")
        @Future(message = "Дата бронирования должна быть в будущем")
        LocalDateTime bookingDateTime,
        
        @NotNull(message = "Количество гостей обязательно")
        @Min(value = 1, message = "Количество гостей должно быть не менее 1")
        Integer numberOfGuests,
        
        @Size(max = 500, message = "Комментарий не должен превышать 500 символов")
        String specialRequests
) {}
