package com.my.mvc.project.mymvcproject.controller.socket;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.dto.EventDto;
import com.my.mvc.project.mymvcproject.model.Event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class WebSocketListener {
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper mapper;
    // private final RequestContext requestContext;

    @EventListener(Event.class)
    @Scope(value = "singleton")
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

    Stream<WebSocketSession> getOpenWebSockets() {
        var openClosedSessions = sessions.stream().collect(Collectors.partitioningBy(WebSocketSession::isOpen));
        openClosedSessions.get(false).forEach(sessions::remove);
        log.info("clearing closed sessions before sending event");
        return openClosedSessions.get(true).stream();
    }

    Stream<WebSocketSession> getUserOpenWebSockets() {
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