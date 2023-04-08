package de.kulturUndStress.spring6imageCaption.controller;

import ai.djl.translate.TranslateException;
import de.kulturUndStress.spring6imageCaption.services.ImageObjectDetectionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/caption")
public class ImageObjectDetectionController {
    private final ImageObjectDetectionService imageObjectDetectionService;

    /**
     * Generates a caption for an image.
     *
     * @param imageName the file name of an image in the images directory
     * @return a caption
     * @throws IOException, TranslateException
     */
    @RequestMapping(value = "{imageName}", method = RequestMethod.GET)
    public String getImageCaption(@PathVariable("imageName") String imageName) throws IOException, TranslateException {
        Path imagePath = Paths.get("images", imageName);
        return imageObjectDetectionService.detect(imagePath);
    }
}
