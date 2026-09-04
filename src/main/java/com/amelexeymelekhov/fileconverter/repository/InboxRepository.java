package com.amelexeymelekhov.fileconverter.repository;

import com.amelexeymelekhov.fileconverter.model.Inbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InboxRepository extends JpaRepository<Inbox, UUID> {
}
