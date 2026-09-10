package com.amelexeymelekhov.fileconverter.kafka;

import com.amelexeymelekhov.fileconverter.dto.FileUploadedEventDTO;
import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import com.amelexeymelekhov.fileconverter.service.FileConverterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUploadedEventConsumer {

    private final FileConverterService fileConverterService;

    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${spring.kafka.consumer.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(String message) {
        try {
            FileUploadedEventDTO dto = objectMapper.readValue(message, FileUploadedEventDTO.class);

            fileConverterService.convertToPdf(dto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(ErrorMessage.FAILED_DESERIALIZE_EVENT.getMessage(), e);
        }
    }
}
