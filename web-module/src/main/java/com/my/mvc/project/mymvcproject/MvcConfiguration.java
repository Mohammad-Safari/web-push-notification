package com.my.mvc.project.mymvcproject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

        /**
         * servlet 3.1 async support, task executer configuration
         */
        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
                executor.setCorePoolSize(1);
                executor.setMaxPoolSize(10);
                executor.setThreadNamePrefix("mvc-task-executor-srv");
                executor.initialize();
                configurer.setTaskExecutor(executor);
        }

        /**
         * this bean leads to async task executing while listening to event
         * so event listener is proxied and it might have side effect
         */
        @Bean
        public SimpleAsyncTaskExecutor getSimpleAsyncTaskExecutor() {
                final SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
                executor.setConcurrencyLimit(10);
                executor.setThreadNamePrefix("event-task-executor-ev");
                return executor;
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
