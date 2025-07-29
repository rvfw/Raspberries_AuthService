package com.example.raspberriesAuthService.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="outbox_events")
@Data
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String aggregateType;
    private Long aggregateId;
    private String eventType;
    private String payload;
    @CreationTimestamp
    private LocalDateTime timestamp;
    private boolean processed;
    public OutboxEvent(String aggregateType, Long aggregateId, String eventType, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
    }

    public OutboxEvent() {

    }
}
