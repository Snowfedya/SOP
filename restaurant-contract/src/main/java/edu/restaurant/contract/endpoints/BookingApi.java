package edu.restaurant.contract.endpoints;

import edu.restaurant.contract.dto.*;
import jakarta.validation.Valid;

/**
 * API для управления бронированиями
 */
public interface BookingApi {

    Object createBooking(@Valid BookingRequest bookingRequest);

    ApiResponse<BookingResponse> getBooking(Long id);

    ApiResponse<BookingResponse> updateBooking(Long id, @Valid BookingRequest bookingRequest);

    ApiResponse<BookingResponse> cancelBooking(Long id);

    ApiResponse<BookingResponse> confirmBooking(Long id);

    ApiResponse<BookingResponse> completeBooking(Long id);

    void deleteBooking(Long id);

    PagedResponse<ApiResponse<BookingResponse>> getAllBookings(
            String guestName,
            String phoneNumber,
            Long tableId,
            BookingStatus status,
            int page,
            int size
    );

    PagedResponse<ApiResponse<BookingResponse>> getBookingsByTable(
            Long tableId,
            int page,
            int size
    );
}
