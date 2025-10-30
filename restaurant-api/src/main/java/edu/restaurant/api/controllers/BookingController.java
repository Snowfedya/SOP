package edu.restaurant.api.controllers;

import edu.restaurant.api.assemblers.BookingModelAssembler;
import edu.restaurant.api.services.BookingService;
import edu.restaurant.contract.dto.*;
import edu.restaurant.contract.endpoints.BookingApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController implements BookingApi {

    private final BookingService service;
    private final BookingModelAssembler assembler;
    private final PagedResourcesAssembler<BookingResponse> pagedAssembler;

    public BookingController(BookingService service, BookingModelAssembler assembler,
                            PagedResourcesAssembler<BookingResponse> pagedAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<BookingResponse>> createBooking(BookingRequest bookingRequest) {
        BookingResponse created = service.create(bookingRequest);
        EntityModel<BookingResponse> model = assembler.toModel(created);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Override
    public EntityModel<BookingResponse> getBooking(Long id) {
        return assembler.toModel(service.findById(id));
    }

    @Override
    public EntityModel<BookingResponse> updateBooking(Long id, BookingRequest bookingRequest) {
        return assembler.toModel(service.update(id, bookingRequest));
    }

    @Override
    public EntityModel<BookingResponse> cancelBooking(Long id) {
        return assembler.toModel(service.cancelBooking(id));
    }

    @Override
    public EntityModel<BookingResponse> confirmBooking(Long id) {
        return assembler.toModel(service.confirmBooking(id));
    }

    @Override
    public EntityModel<BookingResponse> completeBooking(Long id) {
        return assembler.toModel(service.completeBooking(id));
    }

    @Override
    public void deleteBooking(Long id) {
        service.delete(id);
    }

    @Override
    public PagedModel<EntityModel<BookingResponse>> getAllBookings(String guestName, String phoneNumber,
                                                                     Long tableId, BookingStatus status,
                                                                     int page, int size) {
        PagedResponse<BookingResponse> pagedResponse = service.findAll(guestName, phoneNumber, tableId, status, page, size);
        Page<BookingResponse> bookingPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedAssembler.toModel(bookingPage, assembler);
    }

    @Override
    public PagedModel<EntityModel<BookingResponse>> getBookingsByTable(Long tableId, int page, int size) {
        PagedResponse<BookingResponse> pagedResponse = service.findByTableId(tableId, page, size);
        Page<BookingResponse> bookingPage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedAssembler.toModel(bookingPage, assembler);
    }
}
