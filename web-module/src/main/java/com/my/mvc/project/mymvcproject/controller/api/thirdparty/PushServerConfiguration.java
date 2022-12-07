package com.my.mvc.project.mymvcproject.controller.api.thirdparty;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PushServerConfiguration {
    @Value("${third-party.push-server.url}")
    String PUSH_SERVER_URL;
}
