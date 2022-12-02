package com.my.mvc.project.mymvcproject.controller.socket;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.dto.EventDto;
import com.my.mvc.project.mymvcproject.model.Event;
import com.my.mvc.project.mymvcproject.model.User;
import com.my.mvc.project.mymvcproject.service.EventService;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WsMessageController extends TextWebSocketHandler {

    private final ObjectMapper mapper;
    private final UserService userService;
    private final EventService eventService;
    private final WebSocketListener listener;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var eventDto = EventDto.builder().id("0").name("server-notification").data("user subscribed").build();
        log.info("user subscribed sucessfully");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(eventDto)));
        // TODO user session based collection
        // the messages will be broadcasted to all users.
        listener.sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        try {
            var eventDto = mapper.readValue(message.getPayload(), new TypeReference<EventDto>() {
            });
            handleClientEvent(eventDto);
            log.info("event deserialized from websocket channel");
        } catch (IOException e) {
            log.error("deserialization error", e);
        }
        log.info("event sent in websocket channel");
    }

    private void handleClientEvent(EventDto dto) {
        var event = Event.builder()
                .sender(/* userService.getByUsername2(requestContext.getUserContext().getUsername()) */ new User())
                .receiver(/* userService.getByUsername2(dto.getReceiver()) */ new User())
                .data(dto.getData())
                .name(dto.getName()).build();
        eventService.publishCustomEvent(event);

    }
}
