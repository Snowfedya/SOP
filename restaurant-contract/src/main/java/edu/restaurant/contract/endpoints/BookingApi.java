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
 * API для управления бронированиями
 */
@Tag(name = "Бронирования", description = "API для управления бронированиями столиков")
@RequestMapping("/api/v1/bookings")
public interface BookingApi {

    @Operation(summary = "Создать новое бронирование")
    @ApiResponse(responseCode = "201", description = "Бронирование успешно создано")
    @ApiResponse(responseCode = "400", description = "Невалидный запрос", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Столик уже забронирован на это время", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PostMapping
    ResponseEntity<EntityModel<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest bookingRequest);

    @Operation(summary = "Получить бронирование по ID")
    @ApiResponse(responseCode = "200", description = "Бронирование найдено")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @GetMapping("/{id}")
    EntityModel<BookingResponse> getBooking(@PathVariable("id") Long id);

    @Operation(summary = "Обновить данные бронирования")
    @ApiResponse(responseCode = "200", description = "Данные бронирования обновлены")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @ApiResponse(responseCode = "409", description = "Конфликт данных", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PutMapping("/{id}")
    EntityModel<BookingResponse> updateBooking(@PathVariable Long id, @Valid @RequestBody BookingRequest bookingRequest);

    @Operation(summary = "Отменить бронирование")
    @ApiResponse(responseCode = "200", description = "Бронирование отменено")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PatchMapping("/{id}/cancel")
    EntityModel<BookingResponse> cancelBooking(@PathVariable Long id);
    
    @Operation(summary = "Подтвердить бронирование")
    @ApiResponse(responseCode = "200", description = "Бронирование подтверждено")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PatchMapping("/{id}/confirm")
    EntityModel<BookingResponse> confirmBooking(@PathVariable Long id);
    
    @Operation(summary = "Завершить бронирование")
    @ApiResponse(responseCode = "200", description = "Бронирование завершено")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @PatchMapping("/{id}/complete")
    EntityModel<BookingResponse> completeBooking(@PathVariable Long id);

    @Operation(summary = "Удалить бронирование")
    @ApiResponse(responseCode = "204", description = "Бронирование удалено")
    @ApiResponse(responseCode = "404", description = "Бронирование не найдено", 
                 content = @Content(schema = @Schema(implementation = StatusResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteBooking(@PathVariable Long id);

    @Operation(summary = "Получить все бронирования с фильтрацией и пагинацией")
    @ApiResponse(responseCode = "200", description = "Список бронирований получен")
    @GetMapping
    PagedModel<EntityModel<BookingResponse>> getAllBookings(
            @Parameter(description = "Фильтр по имени гостя") @RequestParam(required = false) String guestName,
            @Parameter(description = "Фильтр по телефону") @RequestParam(required = false) String phoneNumber,
            @Parameter(description = "Фильтр по ID столика") @RequestParam(required = false) Long tableId,
            @Parameter(description = "Фильтр по статусу") @RequestParam(required = false) BookingStatus status,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );
    
    @Operation(summary = "Получить бронирования для конкретного столика")
    @ApiResponse(responseCode = "200", description = "Список бронирований для столика получен")
    @GetMapping("/table/{tableId}")
    PagedModel<EntityModel<BookingResponse>> getBookingsByTable(
            @PathVariable Long tableId,
            @Parameter(description = "Номер страницы (0..N)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int size
    );
}
