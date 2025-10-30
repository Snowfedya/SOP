package edu.restaurant.contract.dto;

import java.time.LocalDateTime;

/**
 * DTO для ответа со статусом операции
 */
public record StatusResponse(
        int status,
        String message,
        LocalDateTime timestamp,
        String path
) {}
