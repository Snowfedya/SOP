package edu.restaurant.contract.endpoints;

import edu.restaurant.contract.dto.*;
import jakarta.validation.Valid;

/**
 * API для управления столиками ресторана
 */
public interface TableApi {

    Object createTable(@Valid TableRequest tableRequest);

    ApiResponse<TableResponse> getTable(Long id);

    ApiResponse<TableResponse> updateTable(Long id, @Valid TableRequest tableRequest);

    void deleteTable(Long id);

    PagedResponse<ApiResponse<TableResponse>> getAllTables(
            TableStatus status,
            Integer minCapacity,
            String location,
            int page,
            int size
    );

    PagedResponse<ApiResponse<TableResponse>> getAvailableTables(
            String dateTime,
            Integer minCapacity,
            int page,
            int size
    );
}
