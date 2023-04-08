package de.kulturUndStress.spring6imageCaption.model;

import lombok.Builder;
import lombok.Data;

import java.net.URL;

@Builder
@Data
public class ImageCaption {
    private String caption;
    private URL imageUrl;
}
