package com.amelexeymelekhov.fileconverter.service;

import com.amelexeymelekhov.fileconverter.dto.ExtractFileDTO;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ZipExtractorServiceTest {

    private final ZipExtractorService zipExtractorService = new ZipExtractorService();

    @Test
    void shouldExtractFiles() throws Exception {
        try(InputStream inputStream = getClass().getResourceAsStream("/archive.zip")) {

            assertNotNull(inputStream);

            List<ExtractFileDTO> files = zipExtractorService.extract(inputStream);

            ExtractFileDTO image = files.stream()
                    .filter(f -> f.fileName().equals("image.png"))
                    .findFirst()
                    .orElseThrow();

            ExtractFileDTO text = files.stream()
                    .filter(f -> f.fileName().equals("text.txt"))
                    .findFirst()
                    .orElseThrow();

            assertNotNull(image.content());
            assertNotNull(text.content());
            assertTrue(image.content().length > 0);
            assertTrue(text.content().length > 0);
        }
    }
}
