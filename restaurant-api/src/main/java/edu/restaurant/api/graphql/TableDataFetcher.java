package edu.restaurant.api.graphql;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import edu.restaurant.api.services.TableService;
import edu.restaurant.contract.dto.PagedResponse;
import edu.restaurant.contract.dto.TableResponse;
import lombok.RequiredArgsConstructor;

@DgsComponent
@RequiredArgsConstructor
public class TableDataFetcher {

    private final TableService tableService;

    @DgsQuery
    public PagedResponse<TableResponse> tables(
            @InputArgument String status,
            @InputArgument Integer minCapacity,
            @InputArgument String genre,
            @InputArgument int page,
            @InputArgument int size
    ) {
        return tableService.findAll(status, minCapacity, null, genre, page, size);
    }

    @DgsMutation
    public TableResponse updateTableGenre(@InputArgument Long id, @InputArgument String genre) {
        return tableService.updateTableGenre(id, genre);
    }
}
