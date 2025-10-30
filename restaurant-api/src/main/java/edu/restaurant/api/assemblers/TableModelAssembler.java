package edu.restaurant.api.assemblers;

import edu.restaurant.api.controllers.TableController;
import edu.restaurant.contract.dto.TableResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TableModelAssembler implements RepresentationModelAssembler<TableResponse, EntityModel<TableResponse>> {

    @Override
    public EntityModel<TableResponse> toModel(TableResponse table) {
        return EntityModel.of(table,
                linkTo(methodOn(TableController.class).getTable(table.id())).withSelfRel(),
                linkTo(methodOn(TableController.class).getAllTables(null, null, null, 0, 10)).withRel("tables"));
    }
}
