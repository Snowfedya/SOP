package edu.restaurant.api.graphql;

import com.netflix.graphql.dgs.*;
import edu.restaurant.api.services.BookingService;
import edu.restaurant.api.services.TableService;
import edu.restaurant.contract.dto.*;
import graphql.schema.DataFetchingEnvironment;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@DgsComponent
public class BookingDataFetcher {

    private final BookingService bookingService;
    private final TableService tableService;

    public BookingDataFetcher(BookingService bookingService, TableService tableService) {
        this.bookingService = bookingService;
        this.tableService = tableService;
    }

    @DgsQuery
    public PagedResponse<BookingResponse> bookings(
            @InputArgument String guestName,
            @InputArgument String phoneNumber,
            @InputArgument Long tableId,
            @InputArgument BookingStatus status,
            @InputArgument int page,
            @InputArgument int size) {
        PagedResponse<ApiResponse<BookingResponse>> serviceResponse = bookingService.findAll(guestName, phoneNumber, tableId, status, page, size);
        return new PagedResponse<>(
                serviceResponse.getContent().stream().map(ApiResponse::getData).collect(Collectors.toList()),
                serviceResponse.getPage()
        );
    }

    @DgsQuery
    public BookingResponse bookingById(@InputArgument Long id) {
        return bookingService.findById(id).getData();
    }

    @DgsQuery
    public PagedResponse<BookingResponse> bookingsByTable(
            @InputArgument Long tableId,
            @InputArgument int page,
            @InputArgument int size) {
        PagedResponse<ApiResponse<BookingResponse>> serviceResponse = bookingService.findByTableId(tableId, page, size);
        return new PagedResponse<>(
                serviceResponse.getContent().stream().map(ApiResponse::getData).collect(Collectors.toList()),
                serviceResponse.getPage()
        );
    }

    @DgsMutation
    public BookingResponse createBooking(@InputArgument("input") Map<String, Object> input) {
        BookingRequest request = new BookingRequest(
                (String) input.get("guestName"),
                (String) input.get("phoneNumber"),
                (String) input.get("email"),
                Long.parseLong(input.get("tableId").toString()),
                LocalDateTime.parse((String) input.get("bookingDateTime")),
                (Integer) input.get("numberOfGuests"),
                (String) input.get("specialRequests")
        );
        return bookingService.create(request).getData();
    }

    @DgsMutation
    public BookingResponse updateBooking(@InputArgument Long id, @InputArgument("input") Map<String, Object> input) {
        BookingResponse existing = bookingService.findById(id).getData();
        BookingRequest request = new BookingRequest(
                input.containsKey("guestName") ? (String) input.get("guestName") : existing.guestName(),
                input.containsKey("phoneNumber") ? (String) input.get("phoneNumber") : existing.phoneNumber(),
                input.containsKey("email") ? (String) input.get("email") : existing.email(),
                input.containsKey("tableId") ? Long.parseLong(input.get("tableId").toString()) : existing.tableId(),
                input.containsKey("bookingDateTime") ? LocalDateTime.parse((String) input.get("bookingDateTime")) : existing.bookingDateTime(),
                input.containsKey("numberOfGuests") ? (Integer) input.get("numberOfGuests") : existing.numberOfGuests(),
                input.containsKey("specialRequests") ? (String) input.get("specialRequests") : existing.specialRequests()
        );
        return bookingService.update(id, request).getData();
    }

    @DgsMutation
    public Long deleteBooking(@InputArgument Long id) {
        bookingService.delete(id);
        return id;
    }

    @DgsMutation
    public BookingResponse confirmBooking(@InputArgument Long id) {
        return bookingService.confirmBooking(id).getData();
    }

    @DgsMutation
    public BookingResponse cancelBooking(@InputArgument Long id) {
        return bookingService.cancelBooking(id).getData();
    }

    @DgsMutation
    public BookingResponse completeBooking(@InputArgument Long id) {
        return bookingService.completeBooking(id).getData();
    }

    @DgsData(parentType = "Booking", field = "table")
    public TableResponse table(DataFetchingEnvironment dfe) {
        BookingResponse booking = dfe.getSource();
        return tableService.findById(booking.tableId()).getData();
    }
}
