package de.kulturUndStress.spring6imageCaption.services;
import ai.djl.MalformedModelException;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.translate.TranslateException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.engine.Engine;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.TranslateException;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

@Slf4j
@Service
public class ImageObjectDetectionServiceImpl implements ImageObjectDetectionService {
    Predictor<Image, DetectedObjects> predictor1, predictor2;

    public ImageObjectDetectionServiceImpl() throws ModelNotFoundException, MalformedModelException, IOException {
        var criteria1 = Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION)
                .setTypes(Image.class, DetectedObjects.class)
                .optFilter("backbone", "vgg16")
                .optFilter("flavor", "atrous")
                .optFilter("dataset", "coco")
                .optEngine(Engine.getDefaultEngineName())
                .optArtifactId("ssd")
                .optProgress(new ProgressBar())
                .build();
        this.predictor1 = criteria1.loadModel().newPredictor();

        var criteria2 = Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION)
                .setTypes(Image.class, DetectedObjects.class)
                .optFilter("backbone", "vgg16")
                .optFilter("flavor", "atrous")
                .optFilter("dataset", "voc")
                .optEngine(Engine.getDefaultEngineName())
                .optArtifactId("ssd")
                .optProgress(new ProgressBar())
                .build();
        this.predictor2 = criteria2.loadModel().newPredictor();
    }

    /**
     * Detects objects in an image, using deep java learning library (DJL).
     *
     * @param imagePath String path to an image in the images directory
     * @return caption
     */
    @Override
    public String detect(Path imagePath) throws IOException, TranslateException {
        Image img = ImageFactory.getInstance().fromFile(imagePath);
        DetectedObjects detections1 = this.predictor1.predict(img);
        DetectedObjects detections2 = this.predictor2.predict(img);
        ArrayList<DetectedObjects.DetectedObject> arr = new ArrayList<DetectedObjects.DetectedObject>();
        arr.addAll(detections1.items());
        arr.addAll(detections2.items());
        ArrayList<String> classes = new ArrayList<String>();
        ArrayList<Double> probabilities = new ArrayList<Double>();
        ArrayList<BoundingBox> boundingBoxes = new ArrayList<BoundingBox>();
        for(DetectedObjects.DetectedObject item: arr) {
            classes.add(item.getClassName());
            probabilities.add(item.getProbability());
            boundingBoxes.add(item.getBoundingBox());
        }
        DetectedObjects all = new DetectedObjects(classes, probabilities, boundingBoxes);

        Image newImg = img.duplicate();
        newImg.drawBoundingBoxes(all);
        OutputStream out = new FileOutputStream("images/new.jpg");
        newImg.save(out, "png"); //.save(out, "jpeg");
        out.close();
        return all.toJson();
    }
}
