package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class ImageToGrayscale implements PixelFilter {
    @Override
    public DImage processImage(DImage img) {
        short[][] bw = img.getBWPixelGrid();
        short[][] red = new short[bw.length][bw[0].length];
        short[][] blue = new short[bw.length][bw[0].length];
        short[][] green = new short[bw.length][bw[0].length];

        for (int i = 0; i < bw.length; i++) {
            for (int j = 0; j < bw[0].length; j++) {
                red[i][j] = bw[i][j];
                green[i][j] = bw[i][j];
                blue[i][j] = bw[i][j];
            }
        }

        img.setRedChannel(red);
        img.setBlueChannel(blue);
        img.setGreenChannel(green);
        return img;
    }
}
