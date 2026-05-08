package com.hematovision.controller;

import com.hematovision.model.ClassificationResult;
import com.hematovision.service.BloodCellClassifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BloodCellController.class, HomeController.class})
class BloodCellControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BloodCellClassifier classifier;

    @Test
    void healthShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void classifyShouldReturnPredictionJson() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cell.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());
        when(classifier.classify(any())).thenReturn(new ClassificationResult(
                "neutrophil",
                Map.of("eosinophil", 0.1, "lymphocyte", 0.2, "monocyte", 0.3, "neutrophil", 0.4),
                "aW1hZ2U="
        ));

        mockMvc.perform(multipart("/api/classify").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictedClass").value("neutrophil"))
                .andExpect(jsonPath("$.probabilities.neutrophil").value(0.4))
                .andExpect(jsonPath("$.imageBase64").value("aW1hZ2U="));
    }

    @Test
    void classifyShouldFailForMissingFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "empty.png", MediaType.IMAGE_PNG_VALUE, new byte[0]);

        mockMvc.perform(multipart("/api/classify").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("File upload is required"));
    }
}
