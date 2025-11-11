package edu.restaurant.api.controllers;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class RootController {

    @GetMapping
    public RepresentationModel getRoot() {
        RepresentationModel rootModel = new RepresentationModel<>();
        rootModel.add(
                linkTo(methodOn(TableController.class).getAllTables(0, 10)).withRel("tables"),
                linkTo(methodOn(BookingController.class).getAllBookings(0, 10)).withRel("bookings")
        );
        return rootModel;
    }
}
