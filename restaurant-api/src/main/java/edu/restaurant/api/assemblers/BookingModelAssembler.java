package edu.restaurant.api.assemblers;

import edu.restaurant.api.controllers.BookingController;
import edu.restaurant.api.controllers.TableController;
import edu.restaurant.contract.dto.BookingResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class BookingModelAssembler implements RepresentationModelAssembler<BookingResponse, EntityModel<BookingResponse>> {

    @Override
    public EntityModel<BookingResponse> toModel(BookingResponse booking) {
        return EntityModel.of(booking,
                linkTo(methodOn(BookingController.class).getBooking(booking.id())).withSelfRel(),
                linkTo(methodOn(BookingController.class).getAllBookings(null, null, null, null, 0, 10)).withRel("bookings"),
                linkTo(methodOn(TableController.class).getTable(booking.tableId())).withRel("table"));
    }
}
