package com.example.trackingnumber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TrackingNumberApplication {

    public static void main(String[] args) {
        SpringApplication.run(TrackingNumberApplication.class, args);
    }

}
