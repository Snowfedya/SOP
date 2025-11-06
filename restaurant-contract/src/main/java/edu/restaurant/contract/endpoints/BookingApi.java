package edu.restaurant.contract.endpoints;

import edu.restaurant.contract.dto.*;
import jakarta.validation.Valid;

/**
 * API для управления бронированиями
 */
public interface BookingApi {

    Object createBooking(@Valid BookingRequest bookingRequest);

    ApiResponse<BookingResponse> getBooking(Long id);

    ApiResponse<BookingResponse> cancelBooking(Long id);

    ApiResponse<BookingResponse> confirmBooking(Long id);

    PagedResponse<ApiResponse<BookingResponse>> getAllBookings(
            int page,
            int size
    );
}
