package edu.restaurant.api.assemblers;

import edu.restaurant.contract.dto.ApiResponse;
import edu.restaurant.contract.dto.BookingResponse;
import edu.restaurant.contract.dto.BookingStatus;
import edu.restaurant.contract.dto.Link;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingResponseAssembler {

    public ApiResponse<BookingResponse> toApiResponse(BookingResponse booking) {
        List<Link> links = new ArrayList<>();
        links.add(new Link("self", "/api/v1/bookings/" + booking.id(), "GET"));
        links.add(new Link("table", "/api/v1/tables/" + booking.tableId(), "GET"));

        if (booking.status() == BookingStatus.PENDING) {
            links.add(new Link("confirm", "/api/v1/bookings/" + booking.id() + "/confirm", "PATCH"));
            links.add(new Link("cancel", "/api/v1/bookings/" + booking.id() + "/cancel", "PATCH"));
        }

        if (booking.status() == BookingStatus.CONFIRMED) {
            links.add(new Link("cancel", "/api/v1/bookings/" + booking.id() + "/cancel", "PATCH"));
            links.add(new Link("complete", "/api/v1/bookings/" + booking.id() + "/complete", "PATCH"));
        }

        return new ApiResponse<>(booking, links);
    }
}
