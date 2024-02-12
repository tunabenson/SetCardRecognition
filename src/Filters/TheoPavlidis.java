package Filters;

import Filters.kmeans.Cluster;
import Filters.kmeans.Point;
import Interfaces.PixelFilter;
import core.DImage;

public class TheoPavlidis implements PixelFilter {

    enum Direction{
        UP,DOWN,LEFT,RIGHT;
    }
    @Override
    public DImage processImage(DImage img) {
        img = new ColorReduction(3).processImage(img);
        Cluster[] clusters = new ColorReduction(5).getClusters(img);
        Filters.kmeans.Point val = findClosestToWhite(clusters);
        img = new ColorMasking(30, val.getR(), val.getG(), val.getB()).processImage(img);


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
}
