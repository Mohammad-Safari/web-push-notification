package com.my.mvc.project.mymvcproject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDto {
    private String receiver;
    private String sender;
    private String id;
    private String data;
    @JsonProperty("event")
    private String name;
    private String retry;
}