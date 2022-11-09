package com.my.mvc.project.mymvcproject.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
public class Event {
    @ManyToOne()
    private User receiver;
    @ManyToOne()
    private User sender;
    @Id
    private String id;
    private String data;
    private String type;

}
