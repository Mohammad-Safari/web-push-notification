package com.my.mvc.project.mymvcproject;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.my.mvc.project.mymvcproject.controller.socket.WsMessageController;

import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    WsMessageController wsMessageController;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(wsMessageController, "/socket");
        registry.addHandler(wsMessageController, "/socket").withSockJS();
    }

}