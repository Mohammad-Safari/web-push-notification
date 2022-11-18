package com.my.mvc.project.mymvcproject.controller.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.mvc.project.mymvcproject.context.RequestContext;
import com.my.mvc.project.mymvcproject.dto.EventDto;
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
    RequestContext requestContext;

    @PostMapping("/notification/publish")
    public void publishNotification(@RequestBody EventDto eventDto) {
        Event event = Event.builder()
                .sender(userService.getByUsername2(requestContext.getUserContext().getUsername()))
                .receiver(userService.getByUsername2(eventDto.getReceiver()))
                .data(eventDto.getData())
                .id(eventDto.getId())
                .type(eventDto.getType()).build();
        eventService.publishCustomEvent(event);
    }

}
