package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class SobelEdge implements PixelFilter {
    short[][] newPixelsX = new short[1][1];
    short[][] newPixelsY = new short[1][1];
    @Override
    public DImage processImage(DImage img) {
        short[][] pixels = img.getBWPixelGrid();
        newPixelsX = new short[pixels.length][pixels[0].length];
        newPixelsY = new short[pixels.length][pixels[0].length];
        short[][] kernel = {{1,0,-1},
                            {2,0,-2},
                            {1,0,-1}};
        runConvolution(pixels, newPixelsX, kernel);
        kernel = new short[][]{{-1, -2, -1},
                                {0, 0, 0},
                                {1, 2, 1}};

        runConvolution(pixels, newPixelsY, kernel);

        img.setPixels(combine(newPixelsX, newPixelsY));

        //return ftf.processImage(img);
        return img;
    }
    public void runConvolution(short[][]pixels, short[][] newPixels, short[][] kernel){
        float kernelTotalWeight = calculateKernelWeight(kernel);
        for(int row = 0; row <= pixels.length-(kernel.length); row++){
            for (int col = 0; col <= pixels[0].length-(kernel[0].length); col++) {
                int newPixel = 0;
                for(int r = 0; r < kernel.length; r+=1){
                    for(int c = 0; c < kernel[0].length; c+=1){
                        short kernelVal = kernel[r][c];
                        short original = pixels[r + row][c + col];
                        newPixel += (original * kernelVal);

                    }
                }
                newPixels[row+1][col+1] = clamp((short) (newPixel), (short) 0, (short) 255);



            }
        }
    }
    public float calculateKernelWeight(short[][] kernel){
        int sum = 0;
        for (short[] kernelRow : kernel){
            for (short kernelVal: kernelRow) {
                sum += kernelVal;
            }
        }
        float kernelVal = (float)sum;
        return ( kernelVal > 0) ? kernelVal : (float) 1.0;
    }

    public short clamp(short original, short min, short max){
        if(original < min){
            return min;
        }
        else if(original > max){
            return max;
        }

        return original;
    }

    public short[][] generateKernel(int height, int width, short fill){
        short[][] kernel = new short[height][width];
        for (int i = 0; i < height; i ++) {
            for (int j = 0; j < width; j++) {
                kernel[i][j] = fill;
            }
        }

        return kernel;
    }

    public short[][] combine(short[][] x, short[][] y){
        short[][] newPixels = new short[x.length][x[0].length];
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < x[0].length; j++) {
                newPixels[i][j] = (short) Math.sqrt(x[i][j] * x[i][j] + y[i][j] * y[i][j]);
            }
        }

        return newPixels;
    }


    public double[][] getDirectionAtan2(){
        double[][] newPixels = new double[newPixelsX.length][newPixelsX[0].length];
        for (int i = 0; i < newPixelsX.length; i++) {
            for (int j = 0; j < newPixelsX[0].length; j++) {
                double rad = Math.atan2((double) newPixelsY[i][j],newPixelsX[i][j]);
                if(rad < 0){
                    rad += Math.PI;
                }
                newPixels[i][j] = rad;
            }
        }

        return newPixels;
    }



    private short scaleToFit255(short original, short max){
        return (short) (((double)original/max)*255);
    }

    public double[][] getDirectionSimple() {
        double[][] newPixels = new double[newPixelsX.length][newPixelsX[0].length];
        for (int i = 0; i < newPixelsX.length; i++) {
            for (int j = 0; j < newPixelsX[0].length; j++) {
                double deg = Math.toDegrees(Math.atan((double) newPixelsY[i][j] /newPixelsX[i][j]));
                if(deg > -22.5 && deg <= 22.5){
                    deg = 0;
                }
                else if(deg > 22.5 && deg <= 67.5){
                    deg = 45;
                }
                else if((deg > 67.5 && deg <=90)||(deg > -90 && deg <= -67.5)){
                    deg = 90;
                }
                else if(deg > -67.5 && deg <= -22.5){
                    deg = 135;
                }

                newPixels[i][j] = deg;
            }
        }

        return newPixels;
    }
}
