package com.amelexeymelekhov.fileconverter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record FileConvertedEventDTO(
        @NotNull UUID eventId,
        @NotBlank String bucket,
        @NotBlank String objectName
) {
}
