package com.amelexeymelekhov.fileconverter.service;

import com.amelexeymelekhov.fileconverter.converter.FileConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileConversionServiceTest {

    @Mock
    private FileConverter fileConverter;

    @Mock
    private FileStorageService fileStorageService;

    private FileConversionService converter;

    @BeforeEach
    void setUp() {
        converter = new FileConversionService(
                fileStorageService,
                List.of(fileConverter)
        );
    }

    @Test
    void shouldConvertFileToPdf() {
        String bucket = "file-converter";
        String fileName = "text.txt";
        InputStream file = new ByteArrayInputStream("text".getBytes());
        InputStream convertedFile = new ByteArrayInputStream("pdf".getBytes());

        when(fileConverter.supports("txt")).thenReturn(true);

        when(fileConverter.convert(file)).thenReturn(convertedFile);

        doNothing()
                .when(fileStorageService)
                .upload(any(), any(), any());

        converter.convert(bucket, fileName, file);

        verify(fileConverter).convert(file);

        verify(fileStorageService).upload(
                convertedFile,
                bucket,
                "text.pdf"
        );
    }

    @Test
    void shouldThrowExceptionWhenFileTypeIsNotSupported() {
        String bucket = "file-converter";
        String fileName = "text.txt";
        InputStream file = new ByteArrayInputStream("text".getBytes());

        when(fileConverter.supports("txt")).thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> converter.convert(bucket, fileName, file)
        );

        verify(fileConverter, never()).convert(any());
        verify(fileStorageService, never()).upload(any(), any(), any());
    }
}
