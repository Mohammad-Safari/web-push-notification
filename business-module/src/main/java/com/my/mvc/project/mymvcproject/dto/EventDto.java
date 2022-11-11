package com.my.mvc.project.mymvcproject.dto;

import lombok.Data;

@Data
public class EventDto {
    private String receiver;
    private String sender;
    private String id;
    private String data;
    private String type;
}