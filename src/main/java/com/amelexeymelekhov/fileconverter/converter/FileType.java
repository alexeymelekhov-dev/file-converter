package com.amelexeymelekhov.fileconverter.converter;

import lombok.Getter;

@Getter
public enum FileType {

    TXT("txt"),
    PNG("png"),
    JPG("jpg"),
    ZIP("zip");

    private final String extension;


    FileType(String extension) {
        this.extension = extension;
    }
}
