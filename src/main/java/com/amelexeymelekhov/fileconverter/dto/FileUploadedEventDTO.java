package com.amelexeymelekhov.fileconverter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record FileUploadedEventDTO(
        @NotNull UUID eventId,
        @NotBlank String bucket,
        @NotBlank String objectName
) {
}
