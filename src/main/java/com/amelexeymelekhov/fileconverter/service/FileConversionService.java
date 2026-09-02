package com.amelexeymelekhov.fileconverter.service;

import com.amelexeymelekhov.fileconverter.converter.FileConverter;
import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileConversionService {

    private final FileStorageService fileStorageService;
    private final List<FileConverter> fileConverters;

    public String convert(String bucket, String fileName, InputStream file) {

        String fileType = getFileExtension(fileName);

        FileConverter converter = findConverter(fileType);

        InputStream convertedFile = converter.convert(file);

        return uploadPdf(
                bucket,
                fileName,
                convertedFile
        );

    }

    private FileConverter findConverter(String fileType) {
        return fileConverters.stream()
                .filter(converter -> converter.supports(fileType))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                ErrorMessage.UNSUPPORTED_FILE_TYPE.format(fileType)
                        )
                );
    }

    private String uploadPdf(
            String bucket,
            String fileName,
            InputStream convertedFile
    ) {
        String pdfFileName = getFileNameWithoutExtension(fileName) + ".pdf";

        fileStorageService.upload(
                convertedFile,
                bucket,
                pdfFileName
        );

        return pdfFileName;
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

    private String getFileNameWithoutExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, dotIndex);
    }
}
