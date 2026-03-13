package com.example.Profenaa_touch.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReactRouterController {

    @GetMapping(value = {
            "/admin",
            "/admin/**",
            "/dashboard",
            "/dashboard/**",
            "/course",
            "/course/**",
            "/video",
            "/video/**"
    })
    public String redirect() {
        return "forward:/index.html";
    }
}