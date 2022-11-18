package com.my.mvc.project.mymvcproject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/**")
                                .addResourceLocations(new String[] {
                                                "classpath:/META-INF/resources/",
                                                "classpath:/resources/",
                                                "classpath:/static/",
                                                "classpath:/public/",
                                                "resources/",
                                });
                registry.addResourceHandler("/app/**", "/angular-app/**")
                                .addResourceLocations(new String[] {
                                                "classpath:/angular-app/",
                                });
        }

        // @Bean
        // public DefaultCookieSerializerCustomizer defaultCookieSerializer() {
        // DefaultCookieSerializerCustomizer defaultCookieSerializer = (
        // DefaultCookieSerializer cookieSerializer) -> {
        // cookieSerializer.setCookieName("SESSIONID");
        // cookieSerializer.setCookiePath("/");
        // cookieSerializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        // };
        // return defaultCookieSerializer;
        // }

        /**
         * this bean leads to async task executing while listening to event
         * so event listener is proxied and it might have side effect
         */
        @Bean
        public SimpleAsyncTaskExecutor getSimpleAsyncTaskExecutor() {
                return new SimpleAsyncTaskExecutor();
        }

        @Bean(name = "applicationEventMulticaster")
        public ApplicationEventMulticaster simpleApplicationEventMulticaster(
                        SimpleAsyncTaskExecutor simpleAsyncTaskExecutor) {
                SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
                eventMulticaster.setTaskExecutor(simpleAsyncTaskExecutor);
                return eventMulticaster;
        }

        @Bean
        @Scope(value = "prototype")
        public SseEmitter getSseEmitter() {
                return new SseEmitter();
        }

        @Bean
        public ObjectMapper getMapper() {
                return new ObjectMapper();
        }

}
