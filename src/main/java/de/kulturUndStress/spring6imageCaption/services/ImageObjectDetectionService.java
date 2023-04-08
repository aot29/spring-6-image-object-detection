package de.kulturUndStress.spring6imageCaption.services;

import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageObjectDetectionService {
    String detect(Path imageUrl) throws IOException, TranslateException;
}
