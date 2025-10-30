package edu.restaurant.contract.endpoints;

import edu.restaurant.contract.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API для управления столиками ресторана
 */
@Tag(name = "Столики", description = "API для управления столиками ресторана")
@RequestMapping("/api/v1/tables")
public interface TableApi {

    @Operation(summary = "Создать новый столик")
    @ApiResponse(responseCode = "201", description = "Столик успешно создан")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Столик с таким номером уже существует", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    ResponseEntity<EntityModel<TableResponse>> createTable(@Valid @RequestBody TableRequest tableRequest);

    @Operation(summary = "Получить столик по ID")
    @ApiResponse(responseCode = "200", description = "Столик найден")
    @ApiResponse(responseCode = "404", description = "Столик не найден", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<TableResponse> getTable(@PathVariable("id") Long id);

    @Operation(summary = "Обновить данные столика")
    @ApiResponse(responseCode = "200", description = "Данные столика обновлены")
    @ApiResponse(responseCode = "404", description = "Столик не найден", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Конфликт данных", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<TableResponse> updateTable(@PathVariable Long id, @Valid @RequestBody TableRequest tableRequest);

    @Operation(summary = "Удалить столик")
    @ApiResponse(responseCode = "204", description = "Столик удален")
    @ApiResponse(responseCode = "404", description = "Столик не найден", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTable(@PathVariable Long id);

    @Operation(summary = "Получить все столики с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Список столиков получен")
    @GetMapping
    PagedModel<EntityModel<TableResponse>> getAllTables(
            @Parameter(description = "Фильтр по статусу") @RequestParam(required = false) TableStatus status,
            @Parameter(description = "Минимальное количество мест") @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = "Фильтр по расположению") @RequestParam(required = false) String location,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );
    
    @Operation(summary = "Получить доступные столики на определенное время")
    @ApiResponse(responseCode = "200", description = "Список доступных столиков получен")
    @GetMapping("/available")
    PagedModel<EntityModel<TableResponse>> getAvailableTables(
            @Parameter(description = "Дата и время (ISO format)") @RequestParam String dateTime,
            @Parameter(description = "Минимальное количество мест") @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );
}
