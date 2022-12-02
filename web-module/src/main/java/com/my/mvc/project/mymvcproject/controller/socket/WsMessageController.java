package com.my.mvc.project.mymvcproject.controller.socket;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.context.RequestContext;
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
    // private final RequestContext requestContext;

    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var eventDto = EventDto.builder().id("0").name("server-notification").data("user subscribed").build();
        log.info("user subscribed sucessfully");
        session.sendMessage(new TextMessage(mapper.writeValueAsString(eventDto)));
        // TODO user session based collection
        // the messages will be broadcasted to all users.
        sessions.add(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        getOpenWebSockets().forEach(webSocketSession -> {
            try {
                var eventDto = mapper.readValue(message.getPayload(), new TypeReference<EventDto>() {
                });
                handleClientEvent(eventDto);
                log.info("event deserialized from websocket channel");
            } catch (IOException e) {
                log.error("deserialization error", e);
            }
            log.info("event sent in websocket channel");
        });
    }

    private void handleClientEvent(EventDto dto) {
        var event = Event.builder()
                .sender(/* userService.getByUsername2(requestContext.getUserContext().getUsername()) */ new User())
                .receiver(/* userService.getByUsername2(dto.getReceiver()) */ new User())
                .data(dto.getData())
                .name(dto.getName()).build();
        eventService.publishCustomEvent(event);

    }

    @EventListener(Event.class)
    private void handleServerEvent(Event<String> event) {
        if (event.getName().equals("subscription")) {
            log.info("user has already subscribed and event is already handled");
            return;
        }
        // TODO use user scoped sessions
        getOpenWebSockets().forEach(webSocketSession -> {
            var eventDto = EventDto.builder().id(event.getId()).name(event.getName()).data(event.getData()).build();
            try {
                webSocketSession.sendMessage(new TextMessage(mapper.writeValueAsString(eventDto)));
                log.info("event serialized in websocket channel");
            } catch (IOException e) {
                log.error("serialization error", e);
            }
        });
    }

    private Stream<WebSocketSession> getOpenWebSockets() {
        var openClosedSessions = sessions.stream().collect(Collectors.partitioningBy(WebSocketSession::isOpen));
        openClosedSessions.get(false).forEach(sessions::remove);
        log.info("clearing closed sessions before sending event");
        return openClosedSessions.get(true).stream();
    }

    private Stream<WebSocketSession> getUserOpenWebSockets() {
        // TODO still is not user scoped
        var openClosedSessions = sessions.stream().collect(Collectors.partitioningBy(WebSocketSession::isOpen));
        openClosedSessions.get(false).forEach(sessions::remove);
        log.info("clearing closed sessions before sending event");
        return openClosedSessions.get(true).stream();
    }

    @Scheduled(fixedDelay = 1000000)
    private void removeClosedSessions() {
        log.info("scheduled clearing the closed sessions");
        sessions.removeIf(webSocketSession -> !webSocketSession.isOpen());
    }

}
