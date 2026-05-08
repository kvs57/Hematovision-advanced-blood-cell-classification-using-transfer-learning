package com.hematovision.service;

import com.hematovision.model.ClassificationResult;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class BloodCellClassifier {
    private static final String[] CLASS_LABELS = {"eosinophil", "lymphocyte", "monocyte", "neutrophil"};

    private final ModelLoader modelLoader;
    private final ImageProcessor imageProcessor;

    public BloodCellClassifier(ModelLoader modelLoader, ImageProcessor imageProcessor) {
        this.modelLoader = modelLoader;
        this.imageProcessor = imageProcessor;
    }

    public ClassificationResult classify(MultipartFile file) throws IOException {
        byte[] imageBytes = file.getBytes();
        try {
            INDArray input = imageProcessor.preprocess(imageBytes);
            INDArray output = modelLoader.getModel().outputSingle(input).reshape(CLASS_LABELS.length);

            int predictedIndex = output.argMax().getInt(0);
            Map<String, Double> probabilities = new LinkedHashMap<>();
            for (int i = 0; i < CLASS_LABELS.length; i++) {
                probabilities.put(CLASS_LABELS[i], output.getDouble(i));
            }

            return new ClassificationResult(
                    CLASS_LABELS[predictedIndex],
                    probabilities,
                    Base64.getEncoder().encodeToString(imageBytes)
            );
        } catch (Exception e) {
            throw new IllegalStateException("Inference error: " + e.getMessage(), e);
        }
    }
}
