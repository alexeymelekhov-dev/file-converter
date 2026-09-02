package com.amelexeymelekhov.fileconverter.exception;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    FAILED_PUBLISH_EVENT("Failed to publish event: "),
    UNSUPPORTED_FILE_TYPE("Unsupported file type: "),
    FILE_HAS_NO_EXTENSION("File has no extension: "),
    FAILED_SERIALIZE_EVENT("Failed to serialize event"),
    FAILED_DOWNLOAD_FILE("Failed to download file from MinIO"),
    FAILED_UPLOAD_FILE("Failed to upload file"),
    FAILED_EXTRACT_ZIP("Failed to extract ZIP"),
    FAILED_CONVERT_IMAGE("Error to convert Image to PDF"),
    FAILED_CONVERT_TEXT("Error to convert text to PDF");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return message.formatted(args);
    }

}
