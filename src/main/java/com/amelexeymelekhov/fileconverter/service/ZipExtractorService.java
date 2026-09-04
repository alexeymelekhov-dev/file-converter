package com.amelexeymelekhov.fileconverter.service;

import com.amelexeymelekhov.fileconverter.dto.ExtractFileDTO;
import com.amelexeymelekhov.fileconverter.exception.ErrorMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipExtractorService {

    public List<ExtractFileDTO> extract(InputStream zipStream) {
        List<ExtractFileDTO> files = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                files.add(new ExtractFileDTO(
                        entry.getName(),
                        zis.readAllBytes()
                ));
            }
        } catch (IOException e) {
            throw new IllegalStateException(ErrorMessage.FAILED_EXTRACT_ZIP.getMessage(), e);
        }

        return files;
    }
}

