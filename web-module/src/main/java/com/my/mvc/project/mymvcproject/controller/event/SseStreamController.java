package com.my.mvc.project.mymvcproject.controller.event;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Controller
@RequestMapping("/event")
public class SseStreamController {
    @GetMapping("/sse")
    public SseEmitter streamSseMvc() {
        final SseEmitter emitter = new SseEmitter();
        Runnable process = () -> {
            try {
                // for (int i = 0; i < 10; i++) {
                SseEventBuilder event = SseEmitter.event()
                        .data("SSE MVC - " + LocalTime.now().toString())
                        // .id(String.valueOf(i))
                        .id("nadare")
                        .name("sse event - mvc");
                emitter.send(event);
                Thread.sleep(1000);
                // }
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        };
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(process);
        return emitter;
    }
}
