package edu.restaurant.api.services;

import edu.restaurant.api.models.RestaurantTable;
import edu.restaurant.api.repository.RestaurantTableRepository;
import edu.restaurant.contract.dto.PagedResponse;
import edu.restaurant.contract.dto.TableRequest;
import edu.restaurant.contract.dto.TableResponse;
import edu.restaurant.contract.dto.TableStatus;
import edu.restaurant.contract.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TableService {

    private final RestaurantTableRepository tableRepository;

    @Transactional(readOnly = true)
    public PagedResponse<TableResponse> findAll(String status, Integer minCapacity, String location, String genre, int page, int size) {
        Specification<RestaurantTable> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), TableStatus.valueOf(status.toUpperCase())));
            }
            if (minCapacity != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity));
            }
            if (StringUtils.hasText(location)) {
                predicates.add(cb.like(root.get("location"), "%" + location + "%"));
            }
            if (StringUtils.hasText(genre)) {
                predicates.add(cb.equal(root.get("genre"), genre));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<RestaurantTable> tablePage = tableRepository.findAll(spec, PageRequest.of(page, size));
        return new PagedResponse<>(
                tablePage.getContent().stream().map(this::toResponse).toList(),
                new PagedResponse.PageMetadata(
                        tablePage.getSize(),
                        tablePage.getTotalElements(),
                        tablePage.getTotalPages(),
                        tablePage.getNumber()
                )
        );
    }

    @Transactional(readOnly = true)
    public PagedResponse<TableResponse> findAll(int page, int size) {
        return findAll(null, null, null, null, page, size);
    }

    @Transactional(readOnly = true)
    public TableResponse findById(Long id) {
        return tableRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "Table", id));
    }

    public TableResponse create(TableRequest request) {
        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(request.tableNumber());
        table.setCapacity(request.capacity());
        table.setLocation(request.location());
        table.setStatus(request.status());
        table.setGenre(request.genre());
        RestaurantTable saved = tableRepository.save(table);
        return toResponse(saved);
    }

    public TableResponse update(Long id, TableRequest request) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "Table", id));
        table.setTableNumber(request.tableNumber());
        table.setCapacity(request.capacity());
        table.setLocation(request.location());
        table.setStatus(request.status());
        table.setGenre(request.genre());
        RestaurantTable updated = tableRepository.save(table);
        return toResponse(updated);
    }

    public TableResponse updateTableGenre(Long id, String genre) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found", "Table", id));
        table.setGenre(genre);
        RestaurantTable updated = tableRepository.save(table);
        return toResponse(updated);
    }

    public void delete(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Table not found", "Table", id);
        }
        tableRepository.deleteById(id);
    }

    private TableResponse toResponse(RestaurantTable table) {
        return new TableResponse(
                table.getId(),
                table.getTableNumber(),
                table.getCapacity(),
                table.getLocation(),
                table.getStatus(),
                table.getGenre()
        );
    }
}
