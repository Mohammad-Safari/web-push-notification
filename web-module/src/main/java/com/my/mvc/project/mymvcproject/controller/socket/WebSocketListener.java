package com.my.mvc.project.mymvcproject.controller.socket;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.dto.EventDto;
import com.my.mvc.project.mymvcproject.exceptions.UserNotRegisteredDetailsException;
import com.my.mvc.project.mymvcproject.exceptions.UserNotValidException;
import com.my.mvc.project.mymvcproject.model.Event;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
class WebSocketListener {
    protected final ObjectMapper mapper;
    protected final UserService userService;
    protected final List<WebSocketSession> sessions;

    @EventListener(Event.class)
    private void handleServerEvent(Event<String> event)
            throws UserNotValidException, UserNotRegisteredDetailsException {
        var username = userService.getUserDetails(event.getReceiver().getId()).getUsername();
        getUserOpenWebSockets(username).forEach(webSocketSession -> {
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

    Stream<WebSocketSession> getUserOpenWebSockets(String username) {
        var predicate = (Predicate<WebSocketSession>) (session) -> session.getAttributes().get("contextUser").toString()
                .equals(username);
        var openClosedSessions = sessions.stream().filter(predicate)
                .collect(Collectors.partitioningBy(WebSocketSession::isOpen));
        openClosedSessions.get(false).forEach(sessions::remove);
        log.info("clearing closed sessions before sending event");
        return openClosedSessions.get(true).stream();
    }

    @Scheduled(fixedDelay = 864000)
    private void removeClosedSessions() {
        log.info("scheduled clearing the closed sessions");
        sessions.removeIf(webSocketSession -> !webSocketSession.isOpen());
    }

}