package com.my.mvc.project.mymvcproject;

import org.springframework.boot.autoconfigure.session.DefaultCookieSerializerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
// import org.springframework.session.web.http.DefaultCookieSerializer;

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
                registry.addResourceHandler("/app/**")
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

}
