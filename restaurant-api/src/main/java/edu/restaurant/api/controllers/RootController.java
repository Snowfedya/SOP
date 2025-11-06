package edu.restaurant.api.controllers;

import edu.restaurant.contract.dto.ApiResponse;
import edu.restaurant.contract.dto.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RootController {

    @GetMapping
    public ApiResponse<Map<String, String>> getRoot() {
        List<Link> links = Arrays.asList(
            new Link("tables", "/api/v1/tables", "GET"),
            new Link("bookings", "/api/v1/bookings", "GET")
        );
        return new ApiResponse<>(Map.of("message", "Restaurant API"), links);
    }
}
