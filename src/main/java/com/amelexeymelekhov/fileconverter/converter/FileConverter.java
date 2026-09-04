package com.amelexeymelekhov.fileconverter.converter;

import java.io.InputStream;

public interface FileConverter {

    boolean supports(String fileType);

    InputStream convert(InputStream fileStream);
}
