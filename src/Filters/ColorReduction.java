package Filters;

import Filters.kmeans.Cluster;
import Filters.kmeans.Point;
import Interfaces.PixelFilter;
import core.DImage;

import java.util.Arrays;
import java.util.Collections;

public class ColorReduction implements PixelFilter {

    private int k;
    public ColorReduction(int k){
        this.k = k;
    }
    public ColorReduction(){k = 5;}
    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        Cluster[] clusters = initClusters(k);
        calculateColorCenters(clusters, createPointsArray(red, green, blue));
        setNewColorChannels(clusters,red, green, blue);
        img.setColorChannels(red,green,blue);
        return img;
    }

    public Cluster[] getClusters(DImage img){
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();
        Cluster[] clusters = initClusters(k);
        calculateColorCenters(clusters, createPointsArray(red, green, blue));

        return clusters;
    }
    public void setNewColorChannels(Cluster[] clusters, short[][] red, short[][] green, short[][] blue){
        for (int i = 0; i < red.length; i++) {
            for (int j = 0; j < red[0].length; j++) {
                Point color = getColorForPoint(clusters, new Point(red[i][j], green[i][j], blue[i][j]));
                red[i][j] = color.getR();
                green[i][j] = color.getG();
                blue[i][j] = color.getB();
            }
        }
    }
    private Point getColorForPoint(Cluster[] clusters, Point point) {
        Cluster closest = clusters[0];
        double dist = Integer.MAX_VALUE;
        for (Cluster c : clusters) {
            double cDist = c.getCenter().distanceTo(point);
            if(cDist < dist){
                closest = c;
                dist = cDist;
            }
        }
        return closest.getCenter();
    }

    private Point[] createPointsArray(short[][] red, short[][] green, short[][] blue) {
        Point[] points = new Point[red.length * red[0].length];
        int index = 0;
        for (int i = 0; i < red.length; i++) {
            for (int j = 0; j < red[0].length; j++) {
                points[index] = new Point(red[i][j], green[i][j], blue[i][j]);
                index++;
            }

        }

        return points;
    }

    public Cluster[] initClusters(int k){
        Cluster[] clusters = new Cluster[k];
        double offset = 255.0 / (k+1);
        for (int i = 1; i <= k; i++) {
            short setPoint = (short) (i * offset);
            clusters[i-1] = new Cluster(setPoint);
        }

        return clusters;
    }

    public void calculateColorCenters(Cluster[] clusters, Point[] points){
//        boolean stop = false;
//        while(!stop){
//            clearClusters(clusters);
//            assignPoints(clusters, points);
//            stop = calculateCenters(clusters);
//        }

        for(int i = 0; i < 15; i++){
            clearClusters(clusters);
            assignPoints(clusters, points);
            if(calculateCenters(clusters)){
                return;
            }
        }
    }

    private boolean calculateCenters(Cluster[] clusters) {
        boolean stop = true;
        for (Cluster cluster : clusters) {
            if (!cluster.calculateCenter()) {
                stop = false;
            }
        }

        return stop;
    }

    private void assignPoints(Cluster[] clusters, Point[] points) {
        for (Point p : points) {
            int closestIndex = -1;
            double closestDist = Integer.MAX_VALUE;
            for (int i = 0; i < clusters.length; i++) {
                Point center = clusters[i].getCenter();
                double dist = center.distanceTo(p);
                if(dist < closestDist){
                    closestIndex = i;
                    closestDist = dist;
                }
            }

            clusters[closestIndex].addPoint(p);
        }
    }

    private void clearClusters(Cluster[] clusters) {
        for (Cluster c :
                clusters) {
            c.clear();
        }
    }
}

