package com.amelexeymelekhov.fileconverter.converter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ImageToPdfConverterTest {

    private final ImageToPdfConverter converter = new ImageToPdfConverter();

    @Test
    void shouldSupportImageFormats() {
        assertTrue(converter.supports("png"));
        assertTrue(converter.supports("jpg"));
        assertTrue(converter.supports("jpeg"));
    }

    @Test
    void shouldNotSupportOtherFormats() {
        assertFalse(converter.supports("txt"));
        assertFalse(converter.supports("zip"));
        assertFalse(converter.supports("pdf"));
    }

    @Test
    void shouldConvertPngToPdf() throws Exception {
        try(InputStream inputStream = getClass().getResourceAsStream("/image.png")) {

            assertNotNull(inputStream);

            InputStream result = converter.convert(inputStream);

            assertNotNull(result);

            try (PDDocument document = Loader.loadPDF(result.readAllBytes())) {
                assertEquals(1, document.getNumberOfPages());
            }
        }
    }
}
