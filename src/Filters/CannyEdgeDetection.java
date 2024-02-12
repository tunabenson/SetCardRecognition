package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class CannyEdgeDetection implements PixelFilter {
    @Override
    public DImage processImage(DImage img) {
        DImage gray = new ImageToGrayscale().processImage(img);
        short[][] gaussian = {  {2,4,5,4,2},
                                {4,9,12,9,4},
                                {5,12,15,12,5},
                                {4,9,12,9,4},
                                {2,4,5,4,2}};
        DImage gaus = new Convolution(gaussian).processImage(gray);
        SobelEdge se = new SobelEdge();
        DImage magnitudes = se.processImage(gaus);
//      double[][] direction = se.getDirectionAtan2();
//      img = nonMaximumSuppression(magnitudes.getBWPixelGrid(), direction);
        double[][] directionSimple = se.getDirectionSimple();
        System.out.println(directionSimple.length);
        short[][] nonMaxPixels = nonMaximumSuppressionSimple(magnitudes.getBWPixelGrid(), directionSimple);
        System.out.println(nonMaxPixels.length);
        byte[][] edgeStatus = doubleThreshold(nonMaxPixels, 0.7, 0.1);
        byte[][] hysteriezed = hysteresis(nonMaxPixels, edgeStatus);



        img.setPixels(visualizeEdgeStatus(hysteriezed));
        return img;
    }

    private byte[][] hysteresis(short[][] pixels, byte[][] edgeStatus) {
        byte[][] nEdgePixels = new byte[pixels.length][pixels[0].length];
        for (int i = 0; i < pixels.length-2; i++) {
            for (int j = 0; j < pixels[0].length-2; j++) {
                if(edgeStatus[i][j] == 2 || edgeStatus[i][j] == 0){nEdgePixels[i][j] = edgeStatus[i][j];}
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        if (edgeStatus[i + k][j + l] == 2) {
                            nEdgePixels[i][j] = 2;

                        }
                    }
                }
                if(nEdgePixels[i][j] == 1){
                    nEdgePixels[i][j] = 0;
                }
            }
        }
        return nEdgePixels;
    }

    private short[][] nonMaximumSuppressionSimple(short[][] magnitudes, double[][] directionSimple) {
        short[][] suppressed = new short[magnitudes.length][magnitudes[0].length];
        for (int i = 1; i < magnitudes.length-1; i++) {
            for (int j = 1; j < magnitudes[0].length-1; j++) {
                double angle = directionSimple[i][j];
                short currMag = magnitudes[i][j];
                short p, q;
                if(angle == 0){
                    p = magnitudes[i][j+1];
                    q = magnitudes[i][j-1];
                }
                else if(angle == 90){
                    p = magnitudes[i+1][j];
                    q = magnitudes[i-1][j];
                }
                else if(angle == 45){
                    p = magnitudes[i-1][j+1];
                    q = magnitudes[i+1][j-1];
                }
                else if(angle == 135){
                    p = magnitudes[i-1][j-1];
                    q = magnitudes[i+1][j+1];
                }
                else{
                    p = 255;
                    q = 255;
                }

                if(currMag > p && currMag > q){
                    suppressed[i][j] = currMag;
                }
                else{
                    suppressed[i][j] = 0;
                }
            }
        }
        return suppressed;
    }

    private DImage calculateMagnitudes(short[][] edgeX, short[][] edgeY) {
        DImage img = new DImage(edgeX.length,edgeX[0].length);
        short[][] mags = new short[edgeX.length][edgeX[0].length];
        for (int i = 0; i < edgeY.length; i++) {
            for (int j = 0; j < edgeY[0].length; j++) {
                mags[i][j] = (short) Math.sqrt(Math.pow(edgeX[i][j],2) + Math.pow(edgeY[i][j],2));
            }
        }
        img.setPixels(mags);
        return img;
    }

    private byte[][] doubleThreshold(short[][] pixels, double high, double low){
        System.out.println(pixels.length);
        byte[][] edgeStatus = new byte[pixels.length][pixels[0].length];
        short highVal = (short) (255 * high);
        short lowVal = (short) (255 * low);
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                short curr = pixels[i][j];

                if(curr >= highVal){
                    edgeStatus[i][j] = 2;
                }
                if(curr >= lowVal && curr < highVal){
                    edgeStatus[i][j] = 1;
                }
                if(curr < lowVal){
                    edgeStatus[i][j] = 0;
                }
            }
        }
        return edgeStatus;
    }

    private DImage nonMaximumSuppression(short[][] magnitudes, double[][] directions){
        DImage img = new DImage(magnitudes.length, magnitudes[0].length);
        short[][] suppressed = new short[magnitudes.length][magnitudes[0].length];
        for (int i = 1; i < magnitudes.length-1; i++) {
            for (int j = 1; j < magnitudes[0].length-1; j++) {

                if(directions[i][j] > 0){
                    double alpha = (1/Math.tan(directions[i][j]));
                    //Find q
                    short left = magnitudes[i-1][j];
                    short right = magnitudes[i-1][j+1];
                    short q = (short) ((left * (1-alpha)) + (alpha * right));

                    //Find r
                    left = magnitudes[i+1][j-1];
                    right = magnitudes[i+1][j];
                    short r = (short) ((left * alpha) + ((alpha-1)*right));

                    if(magnitudes[i][j] >= q && magnitudes[i][j] >= r){
                        suppressed[i][j] = 255;
                    }
                    else{
                        suppressed[i][j] = 0;
                    }
                }
                else if(directions[i][j] < 0){
                    double alpha = (1/Math.tan(directions[i][j]));
                    //Find q
                    short left = magnitudes[i-1][j-1];
                    short right = magnitudes[i-1][j];
                    short q = (short) ((left * (1-alpha)) + (alpha * right));

                    //Find r
                    left = magnitudes[i+1][j];
                    right = magnitudes[i+1][j+1];
                    short r = (short) ((left * alpha) + ((alpha-1)*right));

                    if(magnitudes[i][j] >= q && magnitudes[i][j] >= r){
                        suppressed[i][j] = 255;
                    }  else{
                        suppressed[i][j] = 0;
                    }
                }
                else{
                    if(magnitudes[i][j] >= magnitudes[i][j+1] && magnitudes[i][j] >= magnitudes[i][j-1]){
                        suppressed[i][j] = 255;
                    }  else{
                        suppressed[i][j] = 0;
                    }
                }

            }
        }

        img.setPixels(suppressed);
        return img;
    }



    private DImage visualizeDirections(double[][] directions){
        DImage img = new DImage(directions.length, directions[0].length);
        short[][] pixels = new short[directions.length][directions[0].length];

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                double dir = directions[i][j] + Math.PI/2;
                dir = (1/dir);
                pixels[i][j] = (short) (dir * 255);
            }
        }

        img.setPixels(pixels);
        return img;

    }
    private short[][] visualizeEdgeStatus(byte[][] edgeStatus){
        short[][] pixels = new short[edgeStatus.length][edgeStatus[0].length];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                switch(edgeStatus[i][j]){
                    case 1:
                        pixels[i][j] = 80;
                        break;
                    case 2:
                        pixels[i][j] = 200;
                        break;
                    default:
                        pixels[i][j] = 0;
                        break;
                }
            }
        }

        return pixels;
    }



}
