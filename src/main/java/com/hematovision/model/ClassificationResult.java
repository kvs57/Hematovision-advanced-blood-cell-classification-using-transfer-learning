package com.hematovision.model;

import java.util.Map;

public class ClassificationResult {
    private final String predictedClass;
    private final Map<String, Double> probabilities;
    private final String imageBase64;

    public ClassificationResult(String predictedClass, Map<String, Double> probabilities, String imageBase64) {
        this.predictedClass = predictedClass;
        this.probabilities = probabilities;
        this.imageBase64 = imageBase64;
    }

    public String getPredictedClass() {
        return predictedClass;
    }

    public Map<String, Double> getProbabilities() {
        return probabilities;
    }

    public String getImageBase64() {
        return imageBase64;
    }
}
