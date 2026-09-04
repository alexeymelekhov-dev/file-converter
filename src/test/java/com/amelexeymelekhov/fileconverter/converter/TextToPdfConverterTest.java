package com.amelexeymelekhov.fileconverter.converter;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class TextToPdfConverterTest {

    private final TextToPdfConverter converter = new TextToPdfConverter();

    @Test
    void shouldSupportTextFormats() {
        assertTrue(converter.supports("txt"));
    }

    @Test
    void shouldNotSupportOtherFormats() {
        assertFalse(converter.supports("pdf"));
    }

    @Test
    void shouldConvertTextToPdf() throws Exception {
       try(InputStream inputStream = getClass().getResourceAsStream("/text.txt")) {

           assertNotNull(inputStream);

           InputStream result = converter.convert(inputStream);

           assertNotNull(result);

           try (PDDocument document = Loader.loadPDF(result.readAllBytes())) {
               assertEquals(1, document.getNumberOfPages());
           }
       }
    }
}
