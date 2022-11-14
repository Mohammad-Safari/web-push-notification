package com.my.mvc.project.mymvcproject.listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
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
    private final SimpleAsyncTaskExecutor asyncTaskExecutor;
    private ConcurrentHashMap<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, Queue<Event>> queues = new ConcurrentHashMap<>();

    public SseEmitter addEmitter(RequestContext reqContext) {
        log.info("creating emitter...");
        var emitter = context.getBean(SseEmitter.class);
        var userId = userService.getByUsername2(reqContext.getUserContext().getUsername()).getId();
        var emitterList = emitters.getOrDefault(userId, Collections.synchronizedList(new ArrayList<>()));
        if (!emitters.containsKey(userId)) {
            emitters.put(userId, emitterList);
        }
        emitterList.add(emitter);
        emitter.onCompletion(() -> {
            log.info("emitter is being removed caused by completion...");
            emitterList.remove(emitter);
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

    public void retrieveQueuedEvents(RequestContext reqContext) {
        var userId = userService.getByUsername2(reqContext.getUserContext().getUsername()).getId();
        if (queues.getOrDefault(userId, null) != null) {
            log.info("republishing queued events...");
            Queue<Event> queue = queues.get(userId);
            while (!queue.isEmpty()) {
                final var event = queue.poll();
                asyncTaskExecutor.execute(() -> {
                    try {
                        handleEvent(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
        }
    }

    @EventListener(Event.class)
    public void handleEvent(Event event) throws IOException {
        log.warn("Handling event ...");
        var sseEvent = SseEmitter.event()
                .data(event.getData())
                .id(event.getData())
                .name(event.getType());
        var userId = event.getReceiver().getId();
        var emitterList = emitters.getOrDefault(userId, Collections.synchronizedList(new ArrayList<>()));
        if (emitterList.isEmpty()) {
            log.warn("No emitters found for user with id: " + userId);
            var queue = queues.getOrDefault(userId, new LinkedBlockingQueue<>());
            if (!queues.containsKey(userId)) {
                queues.put(userId, queue);
            }
            queue.add(event);
        }
        for (SseEmitter emitter : emitterList) {
            emitter.send(sseEvent);
        }
    }
}
