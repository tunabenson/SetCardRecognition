package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class ImageToGrayscale implements PixelFilter {
    @Override
    public DImage processImage(DImage img) {
        short[][] bw = img.getBWPixelGrid();
        img.setPixels(bw);
        return img;
    }
}
