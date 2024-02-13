package Filters;

import Interfaces.PixelFilter;
import core.DImage;

 class ColorMasking {
    private double threshold =50;
    private short targetR=255, targetG=255, targetB=255;

    public ColorMasking(double threshold, short targetR, short targetG, short targetB) {
        this.threshold = threshold;
        this.targetR = targetR;
        this.targetG = targetG;
        this.targetB = targetB;
    }

    public ColorMasking() {}
    
    public void setThershold(double threshold) {
    	this.threshold=threshold;
    }

    public void processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        for (int i = 0; i < blue.length; i++) {
            for (int j = 0; j < blue[0].length; j++) {
                if(distanceToTarget(red[i][j],green[i][j],blue[i][j])<=threshold) {
                    red[i][j]=255;
                    green[i][j]=255;
                    blue[i][j]=255;
                }
                else {
                    red[i][j]=0;
                    green[i][j]=0;
                    blue[i][j]=0;
                }
            }
        }
        img.setColorChannels(red, green, blue);
    }
    
    private double distanceToTarget(short r, short g, short b) {
        return Math.sqrt(Math.pow(r-targetR, 2)+Math.pow(g-targetG, 2)+Math.pow(b-targetB, 2));
    }
    
    public void changeTarget(short r, short g, short b) {
    	this.targetR=r;
    	this.targetG=g;
    	this.targetB=b;
    }
    public void processImageToFitPrevious(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        for (int i = 0; i < blue.length; i++) {
            for (int j = 0; j < blue[0].length; j++) {
                if(distanceToTarget(red[i][j],green[i][j],blue[i][j])<=threshold) {
                    red[i][j]=255;
                    green[i][j]=255;
                    blue[i][j]=255;
                }
            }
        }
        img.setColorChannels(red, green, blue);
    }
    
    public void processImageToFitPrevious(DImage img, int r, int g, int b) {
    	changeTarget((short)r, (short)g, (short)b);
    	processImageToFitPrevious(img);
    }

}
