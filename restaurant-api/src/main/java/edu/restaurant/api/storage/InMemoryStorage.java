package edu.restaurant.api.storage;

import edu.restaurant.contract.dto.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryStorage {

    public final Map<Long, TableResponse> tables = new ConcurrentHashMap<>();
    public final Map<Long, BookingResponse> bookings = new ConcurrentHashMap<>();

    public final AtomicLong tableSequence = new AtomicLong(0);
    public final AtomicLong bookingSequence = new AtomicLong(0);

    @PostConstruct
    public void init() {
        // Инициализация тестовых столиков
        TableResponse table1 = new TableResponse(
                tableSequence.incrementAndGet(),
                "T001",
                4,
                "У окна, зал А",
                TableStatus.AVAILABLE
        );

        TableResponse table2 = new TableResponse(
                tableSequence.incrementAndGet(),
                "T002",
                2,
                "В углу, зал А",
                TableStatus.AVAILABLE
        );

        TableResponse table3 = new TableResponse(
                tableSequence.incrementAndGet(),
                "T003",
                6,
                "Центр, зал B",
                TableStatus.AVAILABLE
        );

        TableResponse table4 = new TableResponse(
                tableSequence.incrementAndGet(),
                "VIP001",
                8,
                "VIP зона",
                TableStatus.AVAILABLE
        );

        tables.put(table1.id(), table1);
        tables.put(table2.id(), table2);
        tables.put(table3.id(), table3);
        tables.put(table4.id(), table4);

        // Инициализация тестовых бронирований
        BookingResponse booking1 = new BookingResponse(
                bookingSequence.incrementAndGet(),
                "Иван Петров",
                "+79161234567",
                "ivan@example.com",
                1L,
                LocalDateTime.now().plusDays(1).withHour(19).withMinute(0),
                4,
                "Отметить день рождения",
                BookingStatus.CONFIRMED,
                LocalDateTime.now()
        );

        BookingResponse booking2 = new BookingResponse(
                bookingSequence.incrementAndGet(),
                "Мария Сидорова",
                "+79162345678",
                "maria@example.com",
                2L,
                LocalDateTime.now().plusDays(2).withHour(20).withMinute(30),
                2,
                null,
                BookingStatus.PENDING,
                LocalDateTime.now()
        );

        bookings.put(booking1.id(), booking1);
        bookings.put(booking2.id(), booking2);
    }
}
