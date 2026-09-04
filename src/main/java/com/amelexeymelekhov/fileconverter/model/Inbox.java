package com.amelexeymelekhov.fileconverter.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inbox")
@NoArgsConstructor
@Getter
@Setter
public class Inbox {

    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "processed_at", nullable = false)
    private OffsetDateTime processedAt;

    public Inbox(UUID eventId) {
        this.eventId = eventId;
        this.processedAt = OffsetDateTime.now();
    }
}
