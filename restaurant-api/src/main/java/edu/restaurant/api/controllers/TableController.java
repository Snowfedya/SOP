package edu.restaurant.api.controllers;

import edu.restaurant.api.services.TableService;
import edu.restaurant.contract.dto.*;
import edu.restaurant.contract.endpoints.TableApi;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/tables")
public class TableController implements TableApi {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ApiResponse<TableResponse> createTable(@Valid @RequestBody TableRequest tableRequest) {
        return tableService.create(tableRequest);
    }

    @GetMapping("/{id}")
    @Override
    public ApiResponse<TableResponse> getTable(@PathVariable Long id) {
        return tableService.findById(id);
    }

    @PutMapping("/{id}")
    @Override
    public ApiResponse<TableResponse> updateTable(@PathVariable Long id, @Valid @RequestBody TableRequest tableRequest) {
        return tableService.update(id, tableRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void deleteTable(@PathVariable Long id) {
        tableService.delete(id);
    }

    @GetMapping
    @Override
    public PagedResponse<ApiResponse<TableResponse>> getAllTables(
            @RequestParam(required = false) TableStatus status,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return tableService.findAll(status, minCapacity, location, page, size);
    }

    @GetMapping("/available")
    @Override
    public PagedResponse<ApiResponse<TableResponse>> getAvailableTables(
            @RequestParam String dateTime,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LocalDateTime dt = LocalDateTime.parse(dateTime);
        return tableService.findAvailableTables(dt, minCapacity, page, size);
    }
}
