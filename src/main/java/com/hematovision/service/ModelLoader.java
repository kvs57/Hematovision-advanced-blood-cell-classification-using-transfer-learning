package com.hematovision.service;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.modelimport.keras.KerasModelImport;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Component
public class ModelLoader {
    private static final List<Path> MODEL_PATH_CANDIDATES = List.of(
            Path.of("Blood Cell.h5"),
            Path.of("Project Files", "Blood Cell.h5")
    );

    private volatile ComputationGraph model;

    public ComputationGraph getModel() {
        if (model == null) {
            synchronized (this) {
                if (model == null) {
                    model = loadModel();
                }
            }
        }
        return model;
    }

    private ComputationGraph loadModel() {
        Path modelPath = MODEL_PATH_CANDIDATES.stream()
                .filter(Files::exists)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Blood Cell.h5 model not found in expected locations"));

        try {
            return KerasModelImport.importKerasModelAndWeights(modelPath.toString(), false);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load Keras model from: " + modelPath, e);
        }
    }
}
