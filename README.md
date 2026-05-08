# Hematovision-advanced-blood-cell-classification-using-transfer-learning

HematoVision now includes a Java/Spring Boot implementation for blood cell classification using the existing `Blood Cell.h5` model.

## Java Stack
- Spring Boot (REST API + static web UI)
- DL4J model import for Keras `.h5` model inference
- OpenCV Java bindings for image decoding, resizing, and RGB preprocessing
- ND4J for tensor handling

## Classes Predicted
- eosinophil
- lymphocyte
- monocyte
- neutrophil

## Run the Java Application
From repository root:

```bash
mvn spring-boot:run
```

Application URLs:
- UI: `http://localhost:8080/`
- Health: `GET /api/health`
- Classification: `POST /api/classify` (`multipart/form-data`, field: `file`)

## API Response Example
```json
{
  "predictedClass": "neutrophil",
  "probabilities": {
    "eosinophil": 0.02,
    "lymphocyte": 0.11,
    "monocyte": 0.15,
    "neutrophil": 0.72
  },
  "imageBase64": "..."
}
```
