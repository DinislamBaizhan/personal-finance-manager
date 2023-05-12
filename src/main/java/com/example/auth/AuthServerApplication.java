package com.example.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;

@EnableAsync
@SpringBootApplication
public class AuthServerApplication {
    // We can start our application by calling the run method with the primary class

    // The `GetMapping` annotation indicates that this method should be called
    // when handling GET requests to the "/simple-request" endpoint
    @GetMapping("/simple-request")
    public String simpleRequest() {
        System.out.println("simpleRequest was called");
        // In this case, we return the plain text response "ok"
        return "ok";
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

}
