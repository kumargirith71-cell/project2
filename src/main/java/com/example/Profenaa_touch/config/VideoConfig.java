package com.example.Profenaa_touch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class VideoConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry r) {

        // ✅ Course card images (PUBLIC)
        r.addResourceHandler("/course-images/**")
                .addResourceLocations("file:uploads/course-images/")
                .setCachePeriod(0);


        // ✅ Videos (streamed / protected)
        r.addResourceHandler("/videos/**")
                .addResourceLocations("file:uploads/videos/");

        // ✅ Materials (download)
        r.addResourceHandler("/materials/**")
                .addResourceLocations("file:uploads/materials/");

        r.addResourceHandler("/syllabus/**")
                .addResourceLocations("file:uploads/syllabus/");
    }
}
