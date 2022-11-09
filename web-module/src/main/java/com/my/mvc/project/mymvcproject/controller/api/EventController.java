package com.my.mvc.project.mymvcproject.controller.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.mvc.project.mymvcproject.model.Event;
import com.my.mvc.project.mymvcproject.service.EventService;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class EventController {
    UserService userService;
    EventService eventService;

    @RequestMapping("/event")
    public void event() {
        Event event = Event.builder()
                .sender(userService.loadUserByUsername("admin")).receiver(userService.loadUserByUsername("user"))
                .data("data").id("1").type("type").build();
        eventService.publishCustomEvent(event);
    }

}
