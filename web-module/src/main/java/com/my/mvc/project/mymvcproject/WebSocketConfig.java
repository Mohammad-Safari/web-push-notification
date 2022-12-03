package com.my.mvc.project.mymvcproject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.my.mvc.project.mymvcproject.controller.socket.WsMessageHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Lazy
    @Autowired
    WsMessageHandler wsMessageHandler;

    @Bean
    public List<WebSocketSession> webSocketSession() {
        return new CopyOnWriteArrayList<>();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsMessageHandler, "/socket");
        registry.addHandler(wsMessageHandler, "/socket").withSockJS();
    }

}