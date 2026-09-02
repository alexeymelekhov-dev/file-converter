package com.amelexeymelekhov.fileconverter.dto;

public record ExtractFileDTO(
        String fileName,
        byte[] content
) {
}
