package com.my.mvc.project.mymvcproject.controller.event;

import java.io.IOException;

import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.my.mvc.project.mymvcproject.model.Event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
// @Scope("request")
@Slf4j
@AllArgsConstructor
@RequestMapping("/event")
public class PushNotificationController {
    private SseEmitter emitter;

    @EventListener
    public void handleEvent(Event event) throws IOException {
        log.warn("Handling event ...");
        var sseEvent = SseEmitter.event()
                .data(event.getData())
                .id(event.getData())
                .name(event.getType());
        emitter.send(sseEvent);
    }

    @GetMapping("/notification/subscribe")
    public SseEmitter subscribeNotifications() {
        return emitter;
    }
}