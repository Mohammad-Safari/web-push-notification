package com.my.mvc.project.mymvcproject.listener;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.my.mvc.project.mymvcproject.model.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Getter
@Slf4j
public class EventListenerComponent {
    private SseEmitter emitter;

    @Autowired
    public void setEmitter(ApplicationContext context) {
        log.info("creating emitter..");
        this.emitter = context.getBean(SseEmitter.class);
        emitter.onCompletion(() -> {
            log.info("**re**creating emitter...");
            this.emitter = context.getBean(SseEmitter.class);
        });
    }

    @EventListener(Event.class)
    public void handleEvent(Event event) throws IOException {
        log.warn("Handling event ...");
        var sseEvent = SseEmitter.event()
                .data(event.getData())
                .id(event.getData())
                .name(event.getType());
        emitter.send(sseEvent);
    }
}
