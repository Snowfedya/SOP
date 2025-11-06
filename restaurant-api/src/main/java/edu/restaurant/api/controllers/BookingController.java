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

    @PatchMapping("/{id}/cancel")
    @Override
    public ApiResponse<BookingResponse> cancelBooking(@PathVariable Long id) {
        return bookingResponseAssembler.toApiResponse(bookingService.cancel(id));
    }

    @PatchMapping("/{id}/confirm")
    @Override
    public ApiResponse<BookingResponse> confirmBooking(@PathVariable Long id) {
        return bookingResponseAssembler.toApiResponse(bookingService.confirm(id));
    }

    @GetMapping
    @Override
    public PagedResponse<ApiResponse<BookingResponse>> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagedResponse<BookingResponse> pagedResponse = bookingService.findAll(page, size);
        return new PagedResponse<>(
                pagedResponse.getContent().stream().map(bookingResponseAssembler::toApiResponse).collect(Collectors.toList()),
                pagedResponse.getPage()
        );
    }
}
