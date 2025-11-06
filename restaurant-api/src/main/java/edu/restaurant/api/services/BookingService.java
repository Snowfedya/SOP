package edu.restaurant.api.services;

import edu.restaurant.api.models.Booking;
import edu.restaurant.api.models.Guest;
import edu.restaurant.api.models.RestaurantTable;
import edu.restaurant.api.repository.BookingRepository;
import edu.restaurant.api.repository.RestaurantTableRepository;
import edu.restaurant.contract.dto.BookingRequest;
import edu.restaurant.contract.dto.BookingResponse;
import edu.restaurant.contract.dto.PagedResponse;
import edu.restaurant.contract.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RestaurantTableRepository tableRepository;
    private final EventPublishingService eventPublisher;

    @Transactional(readOnly = true)
    public PagedResponse<BookingResponse> findAll(int page, int size) {
        Page<Booking> bookingPage = bookingRepository.findAll(PageRequest.of(page, size));
        return new PagedResponse<>(
                bookingPage.getContent().stream().map(this::toResponse).toList(),
                new PagedResponse.PageMetadata(
                        bookingPage.getSize(),
                        bookingPage.getTotalElements(),
                        bookingPage.getTotalPages(),
                        bookingPage.getNumber()
                )
        );
    }

    @Transactional(readOnly = true)
    public BookingResponse findById(Long id) {
        return bookingRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found", "Booking", id));
    }

    public BookingResponse create(BookingRequest request) {
        RestaurantTable table = tableRepository.findById(request.tableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "Table", request.tableId()));
        
        Guest guest = new Guest();
        guest.setGuestName(request.guestName());
        guest.setPhoneNumber(request.phoneNumber());
        guest.setEmail(request.email());

        Booking booking = new Booking();
        booking.setGuest(guest);
        booking.setTable(table);
        booking.setBookingDateTime(request.bookingDateTime());
        booking.setNumberOfGuests(request.numberOfGuests());
        booking.setSpecialRequests(request.specialRequests());
        booking.setStatus(edu.restaurant.contract.dto.BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        BookingResponse response = toResponse(saved);
        eventPublisher.publishBookingCreated(response);
        return response;
    }

    public BookingResponse confirm(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found", "Booking", id));
        
        booking.confirm();
        Booking saved = bookingRepository.save(booking);
        
        BookingResponse response = toResponse(saved);
        eventPublisher.publishBookingConfirmed(id);

        return response;
    }

    public BookingResponse cancel(Long id) {
        Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found", "Booking", id));

        booking.cancel();
        Booking saved = bookingRepository.save(booking);

        BookingResponse response = toResponse(saved);
        eventPublisher.publishBookingCancelled(id, "Cancelled by user");
        
        return response;
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getGuest().getGuestName(),
                booking.getGuest().getPhoneNumber(),
                booking.getGuest().getGuestName(),
                booking.getTable().getId(),
                booking.getBookingDateTime(),
                booking.getNumberOfGuests(),
                booking.getSpecialRequests(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
