package com.amelexeymelekhov.fileconverter.kafka;

import com.amelexeymelekhov.fileconverter.dto.FileUploadedEventDTO;
import com.amelexeymelekhov.fileconverter.service.FileConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileEventConsumer {

    private final FileConverterService fileConverterService;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(FileUploadedEventDTO dto) {
        fileConverterService.convertToPdf(dto);
    }
}
