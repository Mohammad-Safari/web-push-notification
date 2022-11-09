package com.my.mvc.project.mymvcproject.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.my.mvc.project.mymvcproject.model.Event;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventListenerComponent {
    
    @EventListener
    public void handleEvent(Event event) {
        log.warn("Handling event ...");

    }
}
