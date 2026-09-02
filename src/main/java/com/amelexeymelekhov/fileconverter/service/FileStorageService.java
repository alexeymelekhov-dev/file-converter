package com.amelexeymelekhov.fileconverter.service;

import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final MinioClient minioClient;

    public InputStream download(String bucket, String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAILED_DOWNLOAD_FILE.getMessage(), e);
        }
    }

    public void upload(InputStream fileStream, String bucket, String fileName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(fileStream, -1, 10485760)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(ErrorMessage.FAILED_UPLOAD_FILE.getMessage(), e);
        }
    }

}
