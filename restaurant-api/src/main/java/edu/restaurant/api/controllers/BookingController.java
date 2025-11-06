package edu.restaurant.api.controllers;

import edu.restaurant.api.assemblers.BookingResponseAssembler;
import edu.restaurant.api.services.BookingService;
import edu.restaurant.contract.dto.*;
import edu.restaurant.contract.endpoints.BookingApi;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController implements BookingApi {

    private final BookingService bookingService;
    private final BookingResponseAssembler bookingResponseAssembler;

    public BookingController(BookingService bookingService, BookingResponseAssembler bookingResponseAssembler) {
        this.bookingService = bookingService;
        this.bookingResponseAssembler = bookingResponseAssembler;
    }

    @PostMapping
    @Override
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest bookingRequest) {
        BookingResponse createdBooking = bookingService.create(bookingRequest);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdBooking.id())
                .toUri();
        return ResponseEntity.created(location).body(bookingResponseAssembler.toApiResponse(createdBooking));
    }

    @GetMapping("/{id}")
    @Override
    public ApiResponse<BookingResponse> getBooking(@PathVariable Long id) {
        return bookingResponseAssembler.toApiResponse(bookingService.findById(id));
    }

    @PutMapping("/{id}")
    @Override
    public ApiResponse<BookingResponse> updateBooking(@PathVariable Long id, @Valid @RequestBody BookingRequest bookingRequest) {
        return bookingResponseAssembler.toApiResponse(bookingService.update(id, bookingRequest));
    }

    @PatchMapping("/{id}/cancel")
    @Override
    public ApiResponse<BookingResponse> cancelBooking(@PathVariable Long id) {
        return bookingResponseAssembler.toApiResponse(bookingService.cancelBooking(id));
    }

    @PatchMapping("/{id}/confirm")
    @Override
    public ApiResponse<BookingResponse> confirmBooking(@PathVariable Long id) {
        return bookingResponseAssembler.toApiResponse(bookingService.confirmBooking(id));
    }

    @PatchMapping("/{id}/complete")
    @Override
    public ApiResponse<BookingResponse> completeBooking(@PathVariable Long id) {
        return bookingResponseAssembler.toApiResponse(bookingService.completeBooking(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
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
        PagedResponse<BookingResponse> pagedResponse = bookingService.findAll(guestName, phoneNumber, tableId, status, page, size);
        return new PagedResponse<>(
                pagedResponse.getContent().stream().map(bookingResponseAssembler::toApiResponse).collect(Collectors.toList()),
                pagedResponse.getPage()
        );
    }

    @GetMapping("/table/{tableId}")
    @Override
    public PagedResponse<ApiResponse<BookingResponse>> getBookingsByTable(
            @PathVariable Long tableId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<BookingResponse> pagedResponse = bookingService.findByTableId(tableId, page, size);
        return new PagedResponse<>(
                pagedResponse.getContent().stream().map(bookingResponseAssembler::toApiResponse).collect(Collectors.toList()),
                pagedResponse.getPage()
        );
    }
}
