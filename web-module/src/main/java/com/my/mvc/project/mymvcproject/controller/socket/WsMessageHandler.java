package com.my.mvc.project.mymvcproject.controller.socket;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.dto.EventDto;
import com.my.mvc.project.mymvcproject.model.Event;
import com.my.mvc.project.mymvcproject.service.EventService;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WsMessageHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final UserService userService;
    private final EventService eventService;
    private final List<WebSocketSession> sessions;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var eventDto = EventDto.builder().id("0").name("server-notification").data("user subscribed").build();
        log.info("user subscribed sucessfully");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(eventDto)));
        Principal principal = session.getPrincipal();
        if(principal == null) {
            throw new Exception("user not logged in");
        }
        session.getAttributes().put("contextUser", principal.getName());
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        try {
            var eventDto = mapper.readValue(message.getPayload(), new TypeReference<EventDto>() {
            });
            var contextUser = session.getAttributes().get("contextUser").toString();
            handleClientEvent(eventDto, contextUser);
            log.info("event deserialized from websocket channel");
        } catch (IOException e) {
            log.error("deserialization error", e);
        }
        log.info("event sent in websocket channel");
    }

    private void handleClientEvent(EventDto dto, String contextUser) {
        if (dto.getName().equals("subscription")) {
            log.info("user has already subscribed and event is already handled");
            return;
        }
        var event = Event.builder()
                .sender(userService.getByUsername2(contextUser))
                .receiver(userService.getByUsername2(dto.getReceiver()))
                .data(dto.getData())
                .name(dto.getName()).build();
        eventService.publishCustomEvent(event);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        log.info("user unsubscribed sucessfully");
    }

}
