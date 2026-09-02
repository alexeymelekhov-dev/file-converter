package com.amelexeymelekhov.fileconverter.service;

import com.amelexeymelekhov.fileconverter.dto.ExtractFileDTO;
import com.amelexeymelekhov.fileconverter.dto.FileUploadedEventDTO;
import com.amelexeymelekhov.fileconverter.model.Inbox;
import com.amelexeymelekhov.fileconverter.model.Outbox;
import com.amelexeymelekhov.fileconverter.repository.InboxRepository;
import com.amelexeymelekhov.fileconverter.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileConverterServiceTest {

    @Mock
    private InboxRepository inboxRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private FileConversionService fileConversionService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ZipExtractorService zipExtractorService;

    @InjectMocks
    private FileConverterService fileConverterService;

    @Test
    void shouldConvertToPdf() throws JsonProcessingException {
        FileUploadedEventDTO dto = new FileUploadedEventDTO(
                UUID.randomUUID(),
                "file-converter",
                "text.txt"
        );

        when(inboxRepository.existsById(any())).thenReturn(false);

        when(fileStorageService.download(dto.bucket(), dto.objectName()))
                .thenReturn(new ByteArrayInputStream("text".getBytes()));

        when(fileConversionService.convert(
                eq("file-converter"),
                eq("text.txt"), any())
        ).thenReturn("text.pdf");

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{\"eventId\":\"test\"}");

        fileConverterService.convertToPdf(dto);

        verify(fileStorageService).download("file-converter", "text.txt");

        verify(fileConversionService).convert(
                eq("file-converter"), eq("text.txt"), any());

        verify(outboxRepository).save(any());

        verify(inboxRepository).save(any());
    }

    @Test
    void shouldSkipAlreadyProcessedEvent() {
        FileUploadedEventDTO dto = new FileUploadedEventDTO(
                UUID.randomUUID(),
                "file-converter",
                "text.txt"
        );

        when(inboxRepository.existsById(dto.eventId()))
                .thenReturn(true);

        fileConverterService.convertToPdf(dto);

        verify(fileStorageService, never()).download(any(), any());
        verify(fileConversionService, never()).convert(any(), any(), any());
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void shouldConvertZip() throws JsonProcessingException {
        UUID eventId = UUID.randomUUID();

        FileUploadedEventDTO dto = new FileUploadedEventDTO(
                eventId,
                "file-converter",
                "archive.zip"
        );

        InputStream zip = new ByteArrayInputStream("zip".getBytes());

        ExtractFileDTO file = new ExtractFileDTO(
                "image.png",
                "image".getBytes()
        );

        when(inboxRepository.existsById(eventId)).thenReturn(false);

        when(fileStorageService.download(
                "file-converter",
                "archive.zip"
        )).thenReturn(zip);

        when(zipExtractorService.extract(zip)).thenReturn(List.of(file));

        when(fileConversionService.convert(
                eq("file-converter"),
                eq("image.png"),
                any()
        )).thenReturn("image.pdf");

        when(objectMapper.writeValueAsString(any())).thenReturn("payload");

        fileConverterService.convertToPdf(dto);

        verify(zipExtractorService).extract(zip);

        verify(fileConversionService).convert(
                eq("file-converter"),
                eq("image.png"),
                any()
        );

        verify(outboxRepository).save(any(Outbox.class));

        verify(inboxRepository).save(any(Inbox.class));
    }
}
