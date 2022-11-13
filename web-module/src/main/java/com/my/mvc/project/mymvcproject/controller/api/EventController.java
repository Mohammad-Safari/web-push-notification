package com.my.mvc.project.mymvcproject.controller.api;

import com.my.mvc.project.mymvcproject.dto.EventDto;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping("/notification/publish")
    public void publishNotification(@RequestBody EventDto eventDto) {
        Event event = Event.builder()
                .sender(userService.loadUserByUsername(eventDto.getSender()))
                .receiver(userService.loadUserByUsername(eventDto.getReceiver()))
                .data(eventDto.getData())
                .id(eventDto.getId())
                .type(eventDto.getType()).build();
        eventService.publishCustomEvent(event);
    }

}
