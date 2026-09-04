package com.amelexeymelekhov.fileconverter.kafka;

import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import com.amelexeymelekhov.fileconverter.model.Outbox;
import com.amelexeymelekhov.fileconverter.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventPublisher {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 1000)
    @SchedulerLock(name = "publishPendingEvents", lockAtMostFor = "30s", lockAtLeastFor = "1s")
    public void publishPendingEvents() {
        outboxRepository.findByPublishedAtIsNull()
                .forEach(this::publish);
    }

    private void publish(Outbox outbox) {
        try {
            kafkaTemplate.send(
                    outbox.getTopic(),
                    outbox.getPayload()
            ).get();

            outbox.setPublishedAt(OffsetDateTime.now());
            outboxRepository.save(outbox);
        } catch (Exception e) {
            log.error(ErrorMessage.FAILED_PUBLISH_EVENT.format(outbox.getEventId()), e);
        }
    }
}
