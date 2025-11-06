package edu.restaurant.auditservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RestController
public class AuditEventListener {

    private static final List<AuditRecord> auditLog = Collections.synchronizedList(new ArrayList<>());

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "audit-queue", durable = "true"),
        exchange = @Exchange(name = "restaurant-exchange", type = "topic"),
        key = "#"  // слушаем ВСЕ события
    ))
    public void handleAllEvents(Object event, @Header Map<String, ?> headers) {
        Object correlationIdObj = headers.get("X-Correlation-ID");
        String correlationId = correlationIdObj != null ? correlationIdObj.toString() : "unknown";

        AuditRecord record = AuditRecord.builder()
            .eventType(event.getClass().getSimpleName())
            .eventData(convertToJson(event))
            .timestamp(LocalDateTime.now())
            .correlationId(correlationId)
            .build();

        auditLog.add(record);
        log.info("Audited: type={}, correlationId={}, totalRecords={}",
                 record.getEventType(), correlationId, auditLog.size());
    }

    @GetMapping("/audit/last")
    public List<AuditRecord> getLastAuditRecords() {
        return auditLog.stream()
            .skip(Math.max(0, auditLog.size() - 10))
            .collect(Collectors.toList());
    }

    private String convertToJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class AuditRecord {
    private String eventType;
    private String eventData;
    private LocalDateTime timestamp;
    private String correlationId;
}
