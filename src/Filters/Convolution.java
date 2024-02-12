package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class Convolution implements PixelFilter {

    short[][] kernel;

    public Convolution(short[][] kernel){
        this.kernel = kernel;
    }
    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        short[][] newRed = red.clone();
        short[][] newBlue = blue.clone();
        short[][] newGreen = green.clone();
        runConvolution(red, green, blue, newRed, newGreen, newBlue, kernel);
        img.setColorChannels(newRed,newGreen,newBlue);
        return img;
    }
    public void runConvolution(short[][] red, short[][] green, short[][] blue, short[][] newRed, short[][] newGreen, short[][] newBlue, short[][] kernel){
        float kernelTotalWeight = calculateKernelWeight(kernel);
        for(int row = 0; row <= red.length-(kernel.length); row++){
            for (int col = 0; col <= red[0].length-(kernel[0].length); col++) {
                int outputRed = 0;
                int outputGreen = 0;
                int outputBlue = 0;
                for(int r = 0; r < kernel.length; r+=1){
                    for(int c = 0; c < kernel[0].length; c+=1){
                        short kernelVal = kernel[r][c];
                        short redVal = red[r + row][c + col];
                        short greenVal = green[r + row][c + col];
                        short blueVal = blue[r + row][c + col];
                        outputRed += redVal * kernelVal;
                        outputGreen += greenVal * kernelVal;
                        outputBlue += blueVal * kernelVal;
                    }
                }
                int centerOffsetRow = (kernel.length-1)/2;
                int centerOffsetCol = (kernel[0].length-1)/2;
                newRed[row+centerOffsetRow][col+centerOffsetCol] = clamp((short) (outputRed / kernelTotalWeight), (short) 0, (short) 255);
                newGreen[row+centerOffsetRow][col+centerOffsetCol] = clamp((short) (outputGreen / kernelTotalWeight), (short) 0, (short) 255);
                newBlue[row+centerOffsetRow][col+centerOffsetCol] = clamp((short) (outputBlue / kernelTotalWeight), (short) 0, (short) 255);


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

    public static short[][] generateGaussian(double sigma, int size){
        int k = (size -1)/2;
        short[][] kernel = new short[size][size];

        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++) {
                double firstPart = 1/(2 * Math.PI * Math.pow(sigma,2));
                double secondPart = - ( Math.pow(i-(k+1),2) + Math.pow(j - (k+1),2));
                double thirdPart = 1/(2 * sigma * sigma);
                double fourthPart = secondPart*thirdPart;
                double fifthPart = Math.exp(fourthPart);
                kernel[i-1][j-1] = (short) (firstPart*fifthPart);
            }
        }

        return kernel;
    }
}
