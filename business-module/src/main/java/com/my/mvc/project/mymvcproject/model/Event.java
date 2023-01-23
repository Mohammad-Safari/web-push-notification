package com.my.mvc.project.mymvcproject.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.Builder;
import lombok.Data;

@Data
@Entity
@Builder
@TypeDef(name = "string", typeClass = String.class)
public class Event<T> {
    @ManyToOne()
    private User receiver;
    @ManyToOne
    private User sender;
    @Id
    private String id;
    @Type(type = "string")
    private T data;
    private String name;
}