package com.amelexeymelekhov.fileconverter.converter;

import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import com.amelexeymelekhov.fileconverter.exception.FileConversionException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class TextToPdfConverter implements FileConverter {

    private static final int FONT_SIZE = 12;
    private static final float TEXT_POSITION_X = 50;
    private static final float TEXT_POSITION_Y = 700;
    private static final float LINE_SPACING = 30;

    @Override
    public boolean supports(String fileType) {
        return FileType.TXT.getExtension().equalsIgnoreCase(fileType);
    }

    @Override
    public InputStream convert(InputStream fileStream) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try (PDDocument document = new PDDocument()) {
            byte[] bytes = fileStream.readAllBytes();
            String text = new String(bytes, StandardCharsets.UTF_8);

            String[] lines = text.split("\\r?\\n");

            // Create a new page
            PDPage page = new PDPage();
            document.addPage(page);

            // Create content stream for writing
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // Set font and size
                contentStream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), FONT_SIZE);

                // Begin text block
                contentStream.beginText();

                // Set position (x=50, y=700 from bottom-left)
                contentStream.newLineAtOffset(TEXT_POSITION_X, TEXT_POSITION_Y);

                // Write text
                for (String line : lines) {
                    contentStream.showText(line);
                    // Move to next line
                    contentStream.newLineAtOffset(0, -LINE_SPACING);
                }

                // End text block
                contentStream.endText();
            }

            document.save(out);

            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new FileConversionException(ErrorMessage.FAILED_CONVERT_TEXT.getMessage(), e);
        }
    }
}
