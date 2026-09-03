package com.amelexeymelekhov.fileconverter.converter;

import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import com.amelexeymelekhov.fileconverter.exception.FileConversionException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageToPdfConverter implements FileConverter {

    @Override
    public boolean supports(String fileType) {
        return "png".equalsIgnoreCase(fileType)
                || "jpg".equalsIgnoreCase(fileType)
                || "jpeg".equalsIgnoreCase(fileType);
    }

    @Override
    public InputStream convert(InputStream fileStream) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            // Create a new page
            PDPage page = new PDPage();
            document.addPage(page);

            // Create content stream
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDImageXObject image = PDImageXObject.createFromByteArray(
                        document,
                        fileStream.readAllBytes(),
                        "image"
                );

                // Draw image below the title
                contentStream.drawImage(image, 50, 400, 400, 250);
            }

            document.save(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new FileConversionException(ErrorMessage.FAILED_CONVERT_IMAGE.getMessage(), e);
        }
    }
}
