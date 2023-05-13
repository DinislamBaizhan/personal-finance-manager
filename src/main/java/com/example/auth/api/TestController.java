package com.example.auth.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class TestController {

    @GetMapping("/demo")
    public String simpleRequest() {
        System.out.println("Demo was called");
        // In this case, we return the plain text response "ok"
        return "Demo";
    }
}
