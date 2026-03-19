package com.example.Profenaa_touch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // ✅ existing (keep this)
        registry.addResourceHandler("/course-images/**")
                .addResourceLocations("file:course-images/");

        // 🔥 ADD THIS (VERY IMPORTANT)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/opt/amcurious/uploads/");
    }
}