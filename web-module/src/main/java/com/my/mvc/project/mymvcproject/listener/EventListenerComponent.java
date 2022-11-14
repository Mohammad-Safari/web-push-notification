package com.my.mvc.project.mymvcproject.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.my.mvc.project.mymvcproject.context.RequestContext;
import com.my.mvc.project.mymvcproject.model.Event;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class EventListenerComponent {
    private final ApplicationContext context;
    private final UserService userService;
    private ConcurrentHashMap<Long, ArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(RequestContext reqContext) {
        log.info("creating emitter..");
        var emitter = context.getBean(SseEmitter.class);
        var userId = userService.getByUsername2(reqContext.getUserContext().getUsername()).getId();
        var emitterList = emitters.getOrDefault(userId, new ArrayList<>());
        if (!emitters.containsKey(userId)) {
            emitters.put(userId, emitterList);
        }
        emitterList.add(emitter);
        emitter.onCompletion(() -> {
            log.info("**re**creating emitter...");
            emitterList.remove(emitter);
            emitterList.add(context.getBean(SseEmitter.class));
        });
        emitter.onError((e) -> {
            log.info("emitter is being removed caused by error...");
            emitterList.remove(emitter);
        });
        emitter.onTimeout(() -> {
            log.info("emitter is being removed caused by timeout...");
            emitterList.remove(emitter);
        });
        return emitter;
    }

    @EventListener(Event.class)
    public void handleEvent(Event event) throws IOException {
        log.warn("Handling event ...");
        var sseEvent = SseEmitter.event()
                .data(event.getData())
                .id(event.getData())
                .name(event.getType());
        var userId = event.getReceiver().getId();
        var emitterList = emitters.getOrDefault(userId, new ArrayList<>());
        for (SseEmitter emitter : emitterList) {
            emitter.send(sseEvent);
        }
    }
}
