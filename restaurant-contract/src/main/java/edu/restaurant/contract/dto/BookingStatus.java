package edu.restaurant.contract.dto;

/**
 * Статус бронирования
 */
public enum BookingStatus {
    PENDING,      // Ожидает подтверждения
    CONFIRMED,    // Подтверждено
    CANCELLED,    // Отменено
    COMPLETED,    // Завершено
    NO_SHOW       // Гость не пришел
}
