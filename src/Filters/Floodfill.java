package Filters;


import Filters.kmeans.Cluster;
import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;


import java.awt.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Filter;


public class Floodfill implements PixelFilter, Interactive {

    int x, y;

    @Override
    public DImage processImage(DImage img) {
        img = new ColorReduction(5).processImage(img);
        Cluster[] clusters = new ColorReduction(5).getClusters(img);
        Filters.kmeans.Point val = findClosestToWhite(clusters);
        img = new ColorMasking(30, val.getR(), val.getG(), val.getB()).processImage(img);
        short[][] pixels = img.getBWPixelGrid();
        short floodFillCol = 150;
        //Stack for storing surrounding pixels of the same color
        ArrayList<java.awt.Point> queue = new ArrayList<java.awt.Point>();

        //Get the first white pixel
        Point starting = getStartingPixel(pixels);
        //Point starting = new Point(0,0);
        //queue.add(starting);
        //pixels[starting.x][starting.y] = floodFillCol;

        while(!queue.isEmpty()){
            Point current = queue.remove(0);
            int posX = current.x;
            int posY = current.y;;

            populateWithSurroundingPixels(pixels, current, queue, (short)255, floodFillCol);
        }

        img.setPixels(pixels);
        return img;

    }

    private Filters.kmeans.Point findClosestToWhite(Cluster[] clusters) {
        Filters.kmeans.Point colorWhite = new Filters.kmeans.Point((short)255,(short)255,(short)255);
        Filters.kmeans.Point closeColor = new Filters.kmeans.Point((short)0, (short)0, (short)0);
        double dist = 1000;
        for(int i = 0; i < clusters.length; i++){
            if(clusters[i].getCenter().distanceTo(colorWhite) < dist){
                dist = clusters[i].getCenter().distanceTo(colorWhite);
                closeColor = clusters[i].getCenter();
            }
        }

        return closeColor;
    }

    private void populateWithSurroundingPixels(short[][] pixels, Point current, ArrayList<Point> queue, short oldColor, short newColor) {
        //Check four directions around pixel and see if they are white
        Point up = new Point(current.x -1, current.y);
        Point down = new Point(current.x + 1, current.y);
        Point left = new Point(current.x , current.y-1);
        Point right = new Point(current.x , current.y + 1);
        if(isPixelValidForFloodFill(pixels,up, oldColor, newColor)){
            pixels[up.x][up.y] = newColor;
            queue.add(up);
        }
        if(isPixelValidForFloodFill(pixels,down, oldColor, newColor)){
            pixels[down.x][down.y] = newColor;
            queue.add(down);
        }
        if(isPixelValidForFloodFill(pixels,left, oldColor, newColor)){
            pixels[left.x][left.y] = newColor;
            queue.add(left);
        }
        if(isPixelValidForFloodFill(pixels,right, oldColor, newColor)){
            pixels[right.x][right.y] = newColor;
            queue.add(right);
        }
    }

    private boolean isPixelValidForFloodFill(short[][] pixels, Point pixel, short oldColor, short newColor){
        int x = pixel.x;
        int y = pixel.y;
        if(x < 0 || x >= pixels.length || y < 0 || y >= pixels[0].length || pixels[x][y] != oldColor || pixels[x][y] == newColor){
            return false;
        }

        return true;
    }

    private java.awt.Point getStartingPixel(short[][] pixels) {

        for(int i = 0; i < pixels.length;i++){
            for (int j = 0; j < pixels[0].length; j++) {
                if(pixels[i][j] == 255){
                    return new Point(i,j);
                }
            }
        }

        return new Point(0,0);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        x = mouseX;
        y = mouseY;
    }

    @Override
    public void keyPressed(char key) {

    }
}
