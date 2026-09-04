package com.amelexeymelekhov.fileconverter.service;

import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private FileStorageService fileStorageService;

    @Mock
    private GetObjectResponse getObjectResponse;

    @Mock
    private PutObjectArgs putObjectArgs;

    @Test
    void shouldDownloadFile() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenReturn(getObjectResponse);

        InputStream result = fileStorageService.download(
                "file-converter",
                "image.png"
        );

        assertSame(getObjectResponse, result);

        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void shouldUploadFile() throws Exception {
        InputStream file = new ByteArrayInputStream("test".getBytes());

        fileStorageService.upload(
                file,
                "file-converter",
                "image.pdf"
        );

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void shouldThrowExceptionWhenDownloadFails() throws Exception {
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("MinIO error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> fileStorageService.download(
                        "file-converter",
                        "image.png"
                )
        );

        assertEquals(
                ErrorMessage.FAILED_DOWNLOAD_FILE.getMessage(),
                exception.getMessage()
        );
    }

    @Test
    void shouldThrowExceptionWhenUploadFails() throws Exception {
        doThrow(new RuntimeException("MinIO error"))
                .when(minioClient).putObject(any(PutObjectArgs.class));

        assertThrows(
                RuntimeException.class,
                () -> fileStorageService.upload(
                        new ByteArrayInputStream(
                                "test".getBytes()),
                        "file-converter",
                        "image.pdf"
                )
        );
    }
}
