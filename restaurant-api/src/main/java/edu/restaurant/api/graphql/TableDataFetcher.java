package edu.restaurant.api.graphql;

import com.netflix.graphql.dgs.*;
import edu.restaurant.api.services.TableService;
import edu.restaurant.contract.dto.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@DgsComponent
public class TableDataFetcher {

    private final TableService tableService;

    public TableDataFetcher(TableService tableService) {
        this.tableService = tableService;
    }

    @DgsQuery
    public PagedResponse<TableResponse> tables(
            @InputArgument TableStatus status,
            @InputArgument Integer minCapacity,
            @InputArgument String location,
            @InputArgument int page,
            @InputArgument int size) {
        PagedResponse<ApiResponse<TableResponse>> serviceResponse = tableService.findAll(status, minCapacity, location, page, size);
        return new PagedResponse<>(
                serviceResponse.getContent().stream().map(ApiResponse::getData).collect(Collectors.toList()),
                serviceResponse.getPage()
        );
    }

    @DgsQuery
    public TableResponse tableById(@InputArgument Long id) {
        return tableService.findById(id).getData();
    }

    @DgsQuery
    public PagedResponse<TableResponse> availableTables(
            @InputArgument String dateTime,
            @InputArgument Integer minCapacity,
            @InputArgument int page,
            @InputArgument int size) {
        LocalDateTime dt = LocalDateTime.parse(dateTime);
        PagedResponse<ApiResponse<TableResponse>> serviceResponse = tableService.findAvailableTables(dt, minCapacity, page, size);
        return new PagedResponse<>(
                serviceResponse.getContent().stream().map(ApiResponse::getData).collect(Collectors.toList()),
                serviceResponse.getPage()
        );
    }

    @DgsMutation
    public TableResponse createTable(@InputArgument("input") Map<String, Object> input) {
        TableRequest request = new TableRequest(
                (String) input.get("tableNumber"),
                (Integer) input.get("capacity"),
                (String) input.get("location"),
                TableStatus.valueOf((String) input.get("status"))
        );
        return tableService.create(request).getData();
    }

    @DgsMutation
    public TableResponse updateTable(@InputArgument Long id, @InputArgument("input") Map<String, Object> input) {
        TableRequest request = new TableRequest(
                input.containsKey("tableNumber") ? (String) input.get("tableNumber") : null,
                input.containsKey("capacity") ? (Integer) input.get("capacity") : null,
                input.containsKey("location") ? (String) input.get("location") : null,
                input.containsKey("status") ? TableStatus.valueOf((String) input.get("status")) : null
        );
        return tableService.update(id, request).getData();
    }

    @DgsMutation
    public Long deleteTable(@InputArgument Long id) {
        tableService.delete(id);
        return id;
    }

    @DgsMutation
    public TableResponse updateTableStatus(@InputArgument Long id, @InputArgument TableStatus status) {
        TableResponse existing = tableService.findById(id).getData();
        TableRequest request = new TableRequest(
                existing.tableNumber(),
                existing.capacity(),
                existing.location(),
                status
        );
        return tableService.update(id, request).getData();
    }
}
