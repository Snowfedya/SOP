package edu.restaurant.api.services;

import edu.restaurant.api.storage.InMemoryStorage;
import edu.restaurant.contract.dto.*;
import edu.restaurant.contract.exception.BookingConflictException;
import edu.restaurant.contract.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {
    
    private final InMemoryStorage storage;
    private final TableService tableService;

    public BookingService(InMemoryStorage storage, TableService tableService) {
        this.storage = storage;
        this.tableService = tableService;
    }

    public PagedResponse<ApiResponse<BookingResponse>> findAll(String guestName, String phoneNumber, Long tableId,
                                                                 BookingStatus status, int page, int size) {
        List<BookingResponse> all = storage.bookings.values().stream()
                .filter(b -> guestName == null || b.guestName().toLowerCase().contains(guestName.toLowerCase()))
                .filter(b -> phoneNumber == null || b.phoneNumber().contains(phoneNumber))
                .filter(b -> tableId == null || b.tableId().equals(tableId))
                .filter(b -> status == null || b.status() == status)
                .collect(Collectors.toList());

        int from = Math.max(0, page * size);
        int to = Math.min(all.size(), from + size);
        List<ApiResponse<BookingResponse>> pageContent = all.subList(from, to).stream().map(ApiResponse::new).collect(Collectors.toList());
        long totalElements = all.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return new PagedResponse<>(pageContent, new PagedResponse.PageMetadata(size, totalElements, totalPages, page));
    }

    public ApiResponse<BookingResponse> findById(Long id) {
        BookingResponse booking = storage.bookings.get(id);
        if (booking == null) {
            throw new ResourceNotFoundException("Booking not found with id: " + id, "Booking", id);
        }
        return new ApiResponse<>(booking);
    }

    public ApiResponse<BookingResponse> create(BookingRequest request) {
        // Проверяем существование столика
        tableService.findById(request.tableId());
        
        // Проверяем конфликт бронирований
        checkBookingConflict(request.tableId(), request.bookingDateTime(), null);

        Long id = storage.bookingSequence.incrementAndGet();
        BookingResponse created = new BookingResponse(
                id,
                request.guestName(),
                request.phoneNumber(),
                request.email(),
                request.tableId(),
                request.bookingDateTime(),
                request.numberOfGuests(),
                request.specialRequests(),
                BookingStatus.PENDING,
                LocalDateTime.now()
        );
        storage.bookings.put(id, created);
        return new ApiResponse<>(created);
    }

    public ApiResponse<BookingResponse> update(Long id, BookingRequest request) {
        BookingResponse existing = findById(id).getData();
        
        // Проверяем существование столика
        tableService.findById(request.tableId());
        
        // Проверяем конфликт бронирований (исключая текущее)
        checkBookingConflict(request.tableId(), request.bookingDateTime(), id);

        BookingResponse updated = new BookingResponse(
                id,
                request.guestName(),
                request.phoneNumber(),
                request.email(),
                request.tableId(),
                request.bookingDateTime(),
                request.numberOfGuests(),
                request.specialRequests(),
                existing.status(),
                existing.createdAt()
        );
        storage.bookings.put(id, updated);
        return new ApiResponse<>(updated);
    }

    public void delete(Long id) {
        findById(id);
        storage.bookings.remove(id);
    }

    public ApiResponse<BookingResponse> confirmBooking(Long id) {
        return updateBookingStatus(id, BookingStatus.CONFIRMED);
    }

    public ApiResponse<BookingResponse> cancelBooking(Long id) {
        return updateBookingStatus(id, BookingStatus.CANCELLED);
    }

    public ApiResponse<BookingResponse> completeBooking(Long id) {
        return updateBookingStatus(id, BookingStatus.COMPLETED);
    }

    private ApiResponse<BookingResponse> updateBookingStatus(Long id, BookingStatus newStatus) {
        BookingResponse existing = findById(id).getData();
        BookingResponse updated = new BookingResponse(
                existing.id(),
                existing.guestName(),
                existing.phoneNumber(),
                existing.email(),
                existing.tableId(),
                existing.bookingDateTime(),
                existing.numberOfGuests(),
                existing.specialRequests(),
                newStatus,
                existing.createdAt()
        );
        storage.bookings.put(id, updated);
        return new ApiResponse<>(updated);
    }

    private void checkBookingConflict(Long tableId, LocalDateTime dateTime, Long excludeBookingId) {
        boolean hasConflict = storage.bookings.values().stream()
                .filter(b -> excludeBookingId == null || !b.id().equals(excludeBookingId))
                .filter(b -> b.tableId().equals(tableId))
                .filter(b -> b.status() == BookingStatus.CONFIRMED || b.status() == BookingStatus.PENDING)
                .anyMatch(b -> Math.abs(java.time.Duration.between(b.bookingDateTime(), dateTime).toHours()) < 2);
        
        if (hasConflict) {
            throw new BookingConflictException(
                "Table is already booked at this time",
                tableId,
                dateTime.toString()
            );
        }
    }

    public PagedResponse<ApiResponse<BookingResponse>> findByTableId(Long tableId, int page, int size) {
        List<BookingResponse> all = storage.bookings.values().stream()
                .filter(b -> b.tableId().equals(tableId))
                .collect(Collectors.toList());

        int from = Math.max(0, page * size);
        int to = Math.min(all.size(), from + size);
        List<ApiResponse<BookingResponse>> pageContent = all.subList(from, to).stream().map(ApiResponse::new).collect(Collectors.toList());
        long totalElements = all.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return new PagedResponse<>(pageContent, new PagedResponse.PageMetadata(size, totalElements, totalPages, page));
    }
}
