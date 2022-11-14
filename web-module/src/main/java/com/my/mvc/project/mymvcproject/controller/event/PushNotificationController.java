package com.my.mvc.project.mymvcproject.controller.event;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.my.mvc.project.mymvcproject.context.RequestContext;
import com.my.mvc.project.mymvcproject.listener.EventListenerComponent;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/event")
public class PushNotificationController {
    private RequestContext context;
    private EventListenerComponent eventListenerComponent;

    @GetMapping("/notification/subscribe")
    public SseEmitter subscribeNotifications() {
        SseEmitter emitter = eventListenerComponent.addEmitter(context);
        eventListenerComponent.retrieveQueuedEvents(context);
        return emitter;
    }
}