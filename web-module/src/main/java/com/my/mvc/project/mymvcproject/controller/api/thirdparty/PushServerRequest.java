package com.my.mvc.project.mymvcproject.controller.api.thirdparty;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.mvc.project.mymvcproject.context.RequestContext;
import com.my.mvc.project.mymvcproject.dto.SubscriptionRequestDto;
import com.my.mvc.project.mymvcproject.dto.SubscriptionResponseDto;
import com.my.mvc.project.mymvcproject.exceptions.UserSubscritionNotfoundException;
import com.my.mvc.project.mymvcproject.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/thirdparty")
@RequiredArgsConstructor
public class PushServerRequest {
    private final PushServerConfiguration configuration;
    private final UserService userService;
    private final RequestContext context;
    private final ObjectMapper mapper;
    private ConcurrentHashMap<String, String> userPushId = new ConcurrentHashMap<>();

    @GetMapping("/getSubscriptionUrl")
    public SubscriptionResponseDto createPushServerDto() {
        var username = context.getUserContext().getUsername();
        var uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        uuid = userPushId.putIfAbsent(username, uuid);
        // some connection to third party ¯\_(ツ)_/¯
        var response = new SubscriptionResponseDto();
        response.setUUID(uuid);
        response.setUrl(configuration.PUSH_SERVER_URL + uuid);
        return response;
    }

    @PostMapping("/getSubscriptionUrl")
    public SubscriptionResponseDto getPushServerDto(@RequestBody SubscriptionRequestDto userResolver) throws UserSubscritionNotfoundException {
        var username = userResolver.getUsername();
        var uuid = userPushId.getOrDefault(username,"");
        var response = new SubscriptionResponseDto();
        if(uuid.isEmpty()) {
            throw new UserSubscritionNotfoundException();
        }
        response.setUUID(uuid);
        response.setUrl(configuration.PUSH_SERVER_URL + uuid);
        return response;
    }

}
