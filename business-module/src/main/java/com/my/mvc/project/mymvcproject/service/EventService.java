package com.my.mvc.project.mymvcproject.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.my.mvc.project.mymvcproject.data.repository.EventRepository;
import com.my.mvc.project.mymvcproject.model.Event;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventService {
    private EventRepository eventRepository;
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishCustomEvent(Event event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void saveEvent(Event event) {
        eventRepository.save(event);
    }
}
