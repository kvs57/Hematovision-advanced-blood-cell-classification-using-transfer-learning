package com.hematovision.service;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;

@Component
public class ImageProcessor {
    private static final int IMAGE_WIDTH = 224;
    private static final int IMAGE_HEIGHT = 224;
    private static final int CHANNELS = 3;

    public INDArray preprocess(byte[] imageBytes) {
        Mat source = Imgcodecs.imdecode(new MatOfByte(imageBytes), Imgcodecs.IMREAD_COLOR);
        if (source == null || source.empty()) {
            throw new IllegalArgumentException("Invalid image file");
        }

        Mat resized = new Mat();
        Imgproc.resize(source, resized, new Size(IMAGE_WIDTH, IMAGE_HEIGHT));

        Mat rgb = new Mat();
        Imgproc.cvtColor(resized, rgb, Imgproc.COLOR_BGR2RGB);

        float[] data = new float[IMAGE_WIDTH * IMAGE_HEIGHT * CHANNELS];
        int index = 0;
        for (int y = 0; y < IMAGE_HEIGHT; y++) {
            for (int x = 0; x < IMAGE_WIDTH; x++) {
                double[] pixel = rgb.get(y, x);
                for (int c = 0; c < CHANNELS; c++) {
                    data[index++] = (float) (pixel[c] / 127.5 - 1.0);
                }
            }
        }

        return Nd4j.create(data, new long[]{1, IMAGE_HEIGHT, IMAGE_WIDTH, CHANNELS}, 'c');
    }
}
