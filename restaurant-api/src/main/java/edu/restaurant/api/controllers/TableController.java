package edu.restaurant.api.controllers;

import edu.restaurant.api.assemblers.TableModelAssembler;
import edu.restaurant.api.services.TableService;
import edu.restaurant.contract.dto.*;
import edu.restaurant.contract.endpoints.TableApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/tables")
public class TableController implements TableApi {

    private final TableService service;
    private final TableModelAssembler assembler;
    private final PagedResourcesAssembler<TableResponse> pagedAssembler;

    public TableController(TableService service, TableModelAssembler assembler,
                           PagedResourcesAssembler<TableResponse> pagedAssembler) {
        this.service = service;
        this.assembler = assembler;
        this.pagedAssembler = pagedAssembler;
    }

    @Override
    public ResponseEntity<EntityModel<TableResponse>> createTable(TableRequest tableRequest) {
        TableResponse created = service.create(tableRequest);
        EntityModel<TableResponse> model = assembler.toModel(created);
        return ResponseEntity.created(model.getRequiredLink("self").toUri()).body(model);
    }

    @Override
    public EntityModel<TableResponse> getTable(Long id) {
        return assembler.toModel(service.findById(id));
    }

    @Override
    public EntityModel<TableResponse> updateTable(Long id, TableRequest tableRequest) {
        return assembler.toModel(service.update(id, tableRequest));
    }

    @Override
    public void deleteTable(Long id) {
        service.delete(id);
    }

    @Override
    public PagedModel<EntityModel<TableResponse>> getAllTables(TableStatus status, Integer minCapacity,
                                                                 String location, int page, int size) {
        PagedResponse<TableResponse> pagedResponse = service.findAll(status, minCapacity, location, page, size);
        Page<TableResponse> tablePage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedAssembler.toModel(tablePage, assembler);
    }

    @Override
    public PagedModel<EntityModel<TableResponse>> getAvailableTables(String dateTime, Integer minCapacity,
                                                                       int page, int size) {
        LocalDateTime dt = LocalDateTime.parse(dateTime);
        PagedResponse<TableResponse> pagedResponse = service.findAvailableTables(dt, minCapacity, page, size);
        Page<TableResponse> tablePage = new PageImpl<>(
                pagedResponse.content(),
                PageRequest.of(pagedResponse.pageNumber(), pagedResponse.pageSize()),
                pagedResponse.totalElements()
        );
        return pagedAssembler.toModel(tablePage, assembler);
    }
}
