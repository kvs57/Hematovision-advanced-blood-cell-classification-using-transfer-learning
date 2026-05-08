package com.hematovision.config;

import jakarta.annotation.PostConstruct;
import nu.pattern.OpenCV;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCvConfig {

    @PostConstruct
    public void loadOpenCv() {
        OpenCV.loadLocally();
    }
}
