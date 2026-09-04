package com.amelexeymelekhov.fileconverter.repository;

import com.amelexeymelekhov.fileconverter.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<Outbox, UUID> {
    List<Outbox> findByPublishedAtIsNull();
}
