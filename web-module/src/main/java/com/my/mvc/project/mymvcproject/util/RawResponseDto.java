package com.my.mvc.project.mymvcproject.util;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = RawResponseDto.class)
public abstract class RawResponseDto implements Serializable {
    public RawResponseDto(String... args) {
        init(args);
    }

    public abstract void init(String... args);
}
