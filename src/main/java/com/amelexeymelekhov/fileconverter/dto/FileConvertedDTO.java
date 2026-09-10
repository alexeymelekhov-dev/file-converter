package com.amelexeymelekhov.fileconverter.dto;

import jakarta.validation.constraints.NotBlank;

public record FileConvertedDTO(
        @NotBlank String name
) {
}
