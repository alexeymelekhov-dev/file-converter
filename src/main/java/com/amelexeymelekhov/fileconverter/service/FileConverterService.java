package com.amelexeymelekhov.fileconverter.service;

import com.amelexeymelekhov.fileconverter.dto.ExtractFileDTO;
import com.amelexeymelekhov.fileconverter.dto.FileConvertedEventDTO;
import com.amelexeymelekhov.fileconverter.dto.FileUploadedEventDTO;
import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import com.amelexeymelekhov.fileconverter.model.Inbox;
import com.amelexeymelekhov.fileconverter.model.Outbox;
import com.amelexeymelekhov.fileconverter.repository.InboxRepository;
import com.amelexeymelekhov.fileconverter.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileConverterService {

    private static final String ZIP_FILE_TYPE = "zip";

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    private final FileStorageService fileStorageService;
    private final InboxRepository inboxRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final FileConversionService fileConversionService;
    private final ZipExtractorService zipExtractorService;

    @Transactional
    public void convertToPdf(FileUploadedEventDTO dto) {
        if (isAlreadyProcessed(dto.eventId())) {
            return;
        }

        String fileType = getFileExtension(dto.objectName());

        if (ZIP_FILE_TYPE.equals(fileType)) {
            convertZip(dto);
        } else {
            convertSingleFile(dto);
        }

        saveInbox(dto.eventId());
    }

    private void convertZip(FileUploadedEventDTO dto) {
        InputStream zipFile = fileStorageService.download(
                dto.bucket(),
                dto.objectName()
        );

        List<ExtractFileDTO> extractedFiles = zipExtractorService.extract(zipFile);

        for (ExtractFileDTO file : extractedFiles) {
            if (isSystemFile(file.fileName())) {
                continue;
            }

            convertAndSaveOutbox(
                    dto.eventId(),
                    dto.bucket(),
                    file.fileName(),
                    new ByteArrayInputStream(file.content())
            );
        }
    }

    private void convertSingleFile(FileUploadedEventDTO dto) {
        InputStream file = fileStorageService.download(
                dto.bucket(),
                dto.objectName()
        );

        convertAndSaveOutbox(
                dto.eventId(),
                dto.bucket(),
                dto.objectName(),
                file
        );
    }

    private void convertAndSaveOutbox(
            UUID eventId,
            String bucket,
            String fileName,
            InputStream file
    ) {
        String pdfFileName = fileConversionService.convert(
                bucket,
                fileName,
                file
        );

        FileConvertedEventDTO event = new FileConvertedEventDTO(
                eventId,
                bucket,
                pdfFileName
        );

        saveOutbox(event);
    }

    private boolean isAlreadyProcessed(UUID eventId) {
        return inboxRepository.existsById(eventId);
    }

    private void saveInbox(UUID eventId) {
        inboxRepository.save(new Inbox(eventId));
    }

    private void saveOutbox(FileConvertedEventDTO dto) {
        Outbox outbox = new Outbox();
        outbox.setId(UUID.randomUUID());
        outbox.setEventId(dto.eventId());
        outbox.setTopic(topic);
        outbox.setPayload(serialize(dto));
        outbox.setCreatedAt(OffsetDateTime.now());

        outboxRepository.save(outbox);
    }

    private String serialize(FileConvertedEventDTO event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(ErrorMessage.FAILED_SERIALIZE_EVENT.getMessage(), e);
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            throw new IllegalArgumentException(
                    ErrorMessage.FILE_HAS_NO_EXTENSION.format(fileName)
            );
        }

        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private boolean isSystemFile(String fileName) {
        return fileName.startsWith("__MACOSX/")
                || fileName.startsWith("._");
    }
}
