package edu.restaurant.api.controllers;

import edu.restaurant.api.assemblers.TableResponseAssembler;
import edu.restaurant.api.services.TableService;
import edu.restaurant.contract.dto.*;
import edu.restaurant.contract.endpoints.TableApi;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tables")
public class TableController implements TableApi {

    private final TableService tableService;
    private final TableResponseAssembler tableResponseAssembler;

    public TableController(TableService tableService, TableResponseAssembler tableResponseAssembler) {
        this.tableService = tableService;
        this.tableResponseAssembler = tableResponseAssembler;
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse<TableResponse>> createTable(@Valid @RequestBody TableRequest tableRequest) {
        TableResponse createdTable = tableService.create(tableRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdTable.id())
                .toUri();
        return ResponseEntity.created(location).body(tableResponseAssembler.toApiResponse(createdTable));
    }

    @GetMapping("/{id}")
    @Override
    public ApiResponse<TableResponse> getTable(@PathVariable Long id) {
        return tableResponseAssembler.toApiResponse(tableService.findById(id));
    }

    @PutMapping("/{id}")
    @Override
    public ApiResponse<TableResponse> updateTable(@PathVariable Long id, @Valid @RequestBody TableRequest tableRequest) {
        return tableResponseAssembler.toApiResponse(tableService.update(id, tableRequest));
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
        PagedResponse<TableResponse> pagedResponse = tableService.findAll(status, minCapacity, location, page, size);
        return new PagedResponse<>(
                pagedResponse.getContent().stream().map(tableResponseAssembler::toApiResponse).collect(Collectors.toList()),
                pagedResponse.getPage()
        );
    }

    @GetMapping("/available")
    @Override
    public PagedResponse<ApiResponse<TableResponse>> getAvailableTables(
            @RequestParam String dateTime,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LocalDateTime dt = LocalDateTime.parse(dateTime);
        PagedResponse<TableResponse> pagedResponse = tableService.findAvailableTables(dt, minCapacity, page, size);
        return new PagedResponse<>(
                pagedResponse.getContent().stream().map(tableResponseAssembler::toApiResponse).collect(Collectors.toList()),
                pagedResponse.getPage()
        );
    }
}
