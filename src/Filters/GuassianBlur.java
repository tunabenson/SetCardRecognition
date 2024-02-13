package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class GuassianBlur implements PixelFilter {

    double [][] gaussianBlur ={{0.0625,0.125,0.0625},
            {0.125,0.25,0.125},
            {0.0625,0.125,0.0625}};


    private void applyKernel(int row, int col, short [][] orig, short [][]newImg, float weight, double [][] kernel) {
        float fSum=0;
        for (int i = row-1; i <=row+1; i++) {
            for (int j = col-1; j <=col+1; j++) {
                fSum+= orig[i][j]*((float)kernel[i-row+1][j-col+1]);
            }
        }
        short sum= (short)fSum;
        if(sum>255)sum=255;
        else if (sum<0)sum=0;

        if(weight==0)newImg[row][col]=(short)(sum);
        else newImg[row][col]=(short)(sum/weight);
    }

    @Override
    public DImage processImage(DImage img) {
        float weight=getKernelWeight(gaussianBlur);
        short [][] green= img.getGreenChannel();
        short [][] blue= img.getBlueChannel();
        short [][] red= img.getRedChannel();
        short [][] newGreen= new short[green.length][green[0].length];
        short [][] newBlue= new short[green.length][green[0].length];
        short [][] newRed= new short[green.length][green[0].length];

        for (int i = 1; i < green.length-1; i++) {
            for (int j = 1; j < green[0].length-1; j++) {
                applyKernel(i, j, green, newGreen,weight, gaussianBlur);
                applyKernel(i, j, blue, newBlue,weight, gaussianBlur);
                applyKernel(i, j, red, newRed,weight, gaussianBlur);
            }
        }
        img.setColorChannels(newRed, newGreen, newBlue);
        return img;
    }


    private float getKernelWeight(double [][] kernel) {
        float sum=0;
        for (int i = 0; i < kernel.length; i++) {
            for (int j = 0; j < kernel[0].length; j++) {
                sum+=kernel[i][j];
            }
        }

        return sum;
    }

}