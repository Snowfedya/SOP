package edu.restaurant.api.controllers;

import edu.restaurant.api.services.BookingService;
import edu.restaurant.contract.dto.*;
import edu.restaurant.contract.endpoints.BookingApi;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController implements BookingApi {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Override
    public ApiResponse<BookingResponse> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        return bookingService.create(bookingRequest);
    }

    @GetMapping("/{id}")
    @Override
    public ApiResponse<BookingResponse> getBooking(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @PutMapping("/{id}")
    @Override
    public ApiResponse<BookingResponse> updateBooking(@PathVariable Long id, @Valid @RequestBody BookingRequest bookingRequest) {
        return bookingService.update(id, bookingRequest);
    }

    @PatchMapping("/{id}/cancel")
    @Override
    public ApiResponse<BookingResponse> cancelBooking(@PathVariable Long id) {
        return bookingService.cancelBooking(id);
    }

    @PatchMapping("/{id}/confirm")
    @Override
    public ApiResponse<BookingResponse> confirmBooking(@PathVariable Long id) {
        return bookingService.confirmBooking(id);
    }

    @PatchMapping("/{id}/complete")
    @Override
    public ApiResponse<BookingResponse> completeBooking(@PathVariable Long id) {
        return bookingService.completeBooking(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Override
    public void deleteBooking(@PathVariable Long id) {
        bookingService.delete(id);
    }

    @GetMapping
    @Override
    public PagedResponse<ApiResponse<BookingResponse>> getAllBookings(
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Long tableId,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return bookingService.findAll(guestName, phoneNumber, tableId, status, page, size);
    }

    @GetMapping("/table/{tableId}")
    @Override
    public PagedResponse<ApiResponse<BookingResponse>> getBookingsByTable(
            @PathVariable Long tableId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return bookingService.findByTableId(tableId, page, size);
    }
}
