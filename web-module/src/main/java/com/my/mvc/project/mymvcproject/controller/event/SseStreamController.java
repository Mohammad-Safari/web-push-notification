package com.my.mvc.project.mymvcproject.controller.event;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Controller
@RequestMapping("/event")
public class SseStreamController {
    @GetMapping(path = { "/sse", "/sse/{number}" })
    public SseEmitter streamSseMvc(@PathVariable(value = "number", required = false) Integer number) {
        final SseEmitter emitter = new SseEmitter();
        int finalNumber = number == null ? 10 : number;
        Runnable process = () -> {
            try {
                for (int i = 0; i < finalNumber; i++) {
                    SseEventBuilder event = SseEmitter.event()
                            .name("sse event - mvc")
                            .id(String.valueOf(i))
                            .data("SSE MVC - " + LocalTime.now().toString());
                    emitter.send(event);
                    Thread.sleep(1000);
                }
                SseEventBuilder eventComplete = SseEmitter.event()
                        .name("complete").data("connection is going to be closed");
                emitter.send(eventComplete);
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        };
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(process);
        return emitter;
    }
}
