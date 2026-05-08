package com.hematovision.controller;

import com.hematovision.model.ClassificationResult;
import com.hematovision.service.BloodCellClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
class HomeController {
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
}

@RestController
@RequestMapping("/api")
public class BloodCellController {
    private static final Logger LOGGER = LoggerFactory.getLogger(BloodCellController.class);
    private final BloodCellClassifier classifier;

    public BloodCellController(BloodCellClassifier classifier) {
        this.classifier = classifier;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping(value = "/classify", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> classify(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File upload is required"));
        }

        try {
            ClassificationResult result = classifier.classify(file);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            LOGGER.error("Failed to read uploaded image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process uploaded image"));
        } catch (Exception e) {
            LOGGER.error("Classification failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Model inference failed"));
        }
    }
}
