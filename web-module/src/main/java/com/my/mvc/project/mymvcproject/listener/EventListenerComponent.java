package com.my.mvc.project.mymvcproject.listener;

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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Getter
@Slf4j
public class EventListenerComponent {
    private final ApplicationContext context;
    private final UserService userService;
    private final SimpleAsyncTaskExecutor asyncTaskExecutor;
    private ConcurrentHashMap<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>(); // online user emitters
    private ConcurrentHashMap<Long, Long> integrityId = new ConcurrentHashMap<>(); // incremental id on event sent
    private ConcurrentHashMap<Long, Queue<Event>> queues = new ConcurrentHashMap<>(); // queue for offline user
                                                                                      // notification

    public SseEmitter addEmitter(RequestContext reqContext) {
        log.info("creating emitter...");
        var emitter = context.getBean(SseEmitter.class);
        var userId = userService.getByUsername2(reqContext.getUserContext().getUsername()).getId();
        var emitterList = emitters.getOrDefault(userId, Collections.synchronizedList(new ArrayList<>()));
        emitters.putIfAbsent(userId, emitterList);
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
                    handleEvent(event);
                });
            }
        }
    }

    @SneakyThrows
    public void informConnectionOpening(RequestContext requestContext) {
        var user = userService.getByUsername2(requestContext.getUserContext().getUsername());
        var event = Event.builder().receiver(user).data("connection is opened now").name("open").build();
        asyncTaskExecutor.execute(() -> {
            handleEvent(event);
        });

    }

    @EventListener(Event.class)
    @SneakyThrows
    public void handleEvent(Event event) {
        log.warn("Handling event ...");
        var userId = event.getReceiver().getId();
        var id = integrityId.getOrDefault(userId, 0l);
        var userEmitterList = emitters.getOrDefault(userId, Collections.synchronizedList(new ArrayList<>()));
        if (userEmitterList.isEmpty()) {
            log.warn("No emitters found for user with id: " + userId);
            id = integrityId.put(userId, 0l);
            var queue = queues.getOrDefault(userId, new LinkedBlockingQueue<>());
            queues.putIfAbsent(userId, queue);
            event.setId(String.valueOf(id));
            queue.add(event);
            return;
        }
        var sseEvent = SseEmitter.event()
                .reconnectTime(5000)
                .data(event.getData())
                .id(event.getId())
                .name(event.getName());
        for (SseEmitter emitter : userEmitterList) {
            emitter.send(sseEvent);
        }
        integrityId.put(userId, ++id);
    }
}
