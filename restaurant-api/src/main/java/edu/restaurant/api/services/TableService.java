package edu.restaurant.api.services;

import edu.restaurant.api.storage.InMemoryStorage;
import edu.restaurant.contract.dto.*;
import edu.restaurant.contract.exception.ResourceAlreadyExistsException;
import edu.restaurant.contract.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TableService {
    
    private final InMemoryStorage storage;

    public TableService(InMemoryStorage storage) {
        this.storage = storage;
    }

    public PagedResponse<TableResponse> findAll(TableStatus status, Integer minCapacity, String location, int page, int size) {
        List<TableResponse> all = storage.tables.values().stream()
                .filter(t -> status == null || t.status() == status)
                .filter(t -> minCapacity == null || t.capacity() >= minCapacity)
                .filter(t -> location == null || t.location().toLowerCase().contains(location.toLowerCase()))
                .collect(Collectors.toList());

        int from = Math.max(0, page * size);
        int to = Math.min(all.size(), from + size);
        List<TableResponse> pageContent = all.subList(from, to);
        long totalElements = all.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return new PagedResponse<>(pageContent, new PagedResponse.PageMetadata(size, totalElements, totalPages, page));
    }

    public TableResponse findById(Long id) {
        TableResponse table = storage.tables.get(id);
        if (table == null) {
            throw new ResourceNotFoundException("Table not found with id: " + id, "Table", id);
        }
        return table;
    }

    public TableResponse create(TableRequest request) {
        // Проверка на существование столика с таким номером
        boolean exists = storage.tables.values().stream()
                .anyMatch(t -> t.tableNumber().equals(request.tableNumber()));
        
        if (exists) {
            throw new ResourceAlreadyExistsException(
                "Table with number " + request.tableNumber() + " already exists",
                "Table", request.tableNumber());
        }

        Long id = storage.tableSequence.incrementAndGet();
        TableResponse created = new TableResponse(
                id,
                request.tableNumber(),
                request.capacity(),
                request.location(),
                request.status()
        );
        storage.tables.put(id, created);
        return created;
    }

    public TableResponse update(Long id, TableRequest request) {
        findById(id); // Проверяем существование

        // Проверка на дубликат номера
        boolean duplicateExists = storage.tables.values().stream()
                .anyMatch(t -> !t.id().equals(id) && t.tableNumber().equals(request.tableNumber()));
        
        if (duplicateExists) {
            throw new ResourceAlreadyExistsException(
                "Table with number " + request.tableNumber() + " already exists",
                "Table", request.tableNumber());
        }

        TableResponse updated = new TableResponse(
                id,
                request.tableNumber(),
                request.capacity(),
                request.location(),
                request.status()
        );
        storage.tables.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id); // Проверяем существование
        
        // Удаляем все бронирования для этого столика
        List<Long> bookingsToDelete = storage.bookings.values().stream()
                .filter(b -> b.tableId().equals(id))
                .map(BookingResponse::id)
                .collect(Collectors.toList());
        
        bookingsToDelete.forEach(storage.bookings::remove);
        storage.tables.remove(id);
    }

    public PagedResponse<TableResponse> findAvailableTables(LocalDateTime dateTime, Integer minCapacity, int page, int size) {
        List<TableResponse> all = storage.tables.values().stream()
                .filter(t -> t.status() == TableStatus.AVAILABLE)
                .filter(t -> minCapacity == null || t.capacity() >= minCapacity)
                .filter(t -> !isTableBookedAtTime(t.id(), dateTime))
                .collect(Collectors.toList());

        int from = Math.max(0, page * size);
        int to = Math.min(all.size(), from + size);
        List<TableResponse> pageContent = all.subList(from, to);
        long totalElements = all.size();
        long totalPages = (long) Math.ceil((double) totalElements / size);

        return new PagedResponse<>(pageContent, new PagedResponse.PageMetadata(size, totalElements, totalPages, page));
    }

    private boolean isTableBookedAtTime(Long tableId, LocalDateTime dateTime) {
        return storage.bookings.values().stream()
                .anyMatch(b -> b.tableId().equals(tableId) &&
                        b.status() == BookingStatus.CONFIRMED &&
                        Math.abs(java.time.Duration.between(b.bookingDateTime(), dateTime).toHours()) < 2);
    }
}
