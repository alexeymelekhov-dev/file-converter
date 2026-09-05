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

    private static final float IMAGE_X = 50;
    private static final float IMAGE_Y = 400;
    private static final float IMAGE_WIDTH = 400;
    private static final float IMAGE_HEIGHT = 250;
    private static final String IMAGE_NAME = "image";

    @Override
    public boolean supports(String fileType) {
        return FileType.PNG.getExtension().equalsIgnoreCase(fileType)
                || FileType.JPG.getExtension().equalsIgnoreCase(fileType);
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
                        IMAGE_NAME
                );

                // Draw image below the title
                contentStream.drawImage(image, IMAGE_X, IMAGE_Y, IMAGE_WIDTH, IMAGE_HEIGHT);
            }

            document.save(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new FileConversionException(ErrorMessage.FAILED_CONVERT_IMAGE.getMessage(), e);
        }
    }
}
