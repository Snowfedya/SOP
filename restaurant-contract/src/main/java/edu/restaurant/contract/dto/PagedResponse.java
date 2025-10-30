package edu.restaurant.contract.dto;

import java.util.List;

/**
 * DTO для пагинированного ответа
 */
public record PagedResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean last
) {}
