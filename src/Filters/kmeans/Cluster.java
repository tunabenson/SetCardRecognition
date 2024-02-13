package Filters.kmeans;

import java.util.ArrayList;

public class Cluster {
    private ArrayList<Point> points = new ArrayList<>();
    private Point center;


    public Cluster(){
        center = new Point((short)(Math.random() * 256), (short)(Math.random() * 256), (short)(Math.random() * 256));
    }
    public void addPoint(Point p){
        points.add(p);
    }
    public void clear(){
        points = new ArrayList<>();
    }

    public boolean inCluster(Point p){

        return points.contains(p);
    }
    /**
    *
    * @return True if the center hasn't moved, false if it has
    * */
    public boolean calculateCenter(){
        double meanR = 0, meanG = 0, meanB = 0;

        for (Point p :
                points) {
            meanR += p.getR();
            meanG += p.getG();
            meanB += p.getB();
        }
        Point newCenter =  new Point((short) (meanR/getSize()), (short) (meanG/getSize()), (short) (meanB/getSize()));;
        if(center.distanceTo(newCenter) == 0){
            return true;
        }
        else{
            center = new Point((short) (meanR/getSize()), (short) (meanG/getSize()), (short) (meanB/getSize()));
            return false;
        }

    }
    public Point getCenter(){
        return center;
    }
    public int getSize(){
        return points.size();

    }


}
