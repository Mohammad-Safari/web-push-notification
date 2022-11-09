package com.my.mvc.project.mymvcproject.controller.event;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@Controller
@RequestMapping("/event")
public class ResponseBodyEmitterController {

    private ExecutorService executor = Executors.newCachedThreadPool();

    @GetMapping(value = "/rbe")
    public ResponseEntity<ResponseBodyEmitter> streamRbeMvc(HttpServletResponse response,
            @RequestParam(value = "counter", required = false, defaultValue = "10") Integer counter) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        executor.execute(() -> {
            try {
                for (int i = 0; i < counter; i++) {

                    String eventData = "data:SSE MVC - " + new Date() + "\n" +
                            "id:" + i + "\n" +
                            "event:message\n\n";
                    emitter.send(eventData, MediaType.TEXT_EVENT_STREAM);
                    Thread.sleep(1000);
                }
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }
}