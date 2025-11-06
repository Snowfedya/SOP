package edu.restaurant.api.assemblers;

import edu.restaurant.contract.dto.ApiResponse;
import edu.restaurant.contract.dto.Link;
import edu.restaurant.contract.dto.TableResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TableResponseAssembler {

    public ApiResponse<TableResponse> toApiResponse(TableResponse table) {
        List<Link> links = new ArrayList<>();
        links.add(new Link("self", "/api/v1/tables/" + table.id(), "GET"));
        links.add(new Link("bookings", "/api/v1/tables/" + table.id() + "/bookings", "GET"));

        return new ApiResponse<>(table, links);
    }
}
