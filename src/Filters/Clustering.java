package Filters;

import java.awt.Point;
import java.util.ArrayList;

import ColorReduction.ColorReduction;
import Interfaces.PixelFilter;
import core.DImage;

public class Clustering implements PixelFilter{
	int numberOfCards=9;
	@Override
	public DImage processImage(DImage img) {
		img= new ColorReduction().processImage(img);
		img= new GuassianBlur().processImage(img);
		img= new ColorMasking().processImage(img);
		
		Clusters.loadImage(img);
		Cluster [] list= new Cluster[numberOfCards];
		Point [] pList=Clusters.getInitialLocations(numberOfCards);
		for (int i = 0; i < list.length; i++) {
			Point p=pList[i];
			list[i]= new Cluster(p.x, p.y);
		}
		Clusters.loadClusters(list);

		while(!Clusters.isStable()) {
			Clusters.assignPoints();
			Clusters.clearAll();
		}
		Clusters.assignPoints();
		return Clusters.getImage();

	}
}

class Cluster {
	private Point previousCenter;
	private Point center; 
	private ArrayList<Point> pointList;

	public Cluster(int x, int y) {
		previousCenter= new Point(-1,-1);
		center= new Point(x,y); //add coordinates
		pointList= new ArrayList<>();
	}

	public void add(Point p) {
		pointList.add(p);
	}

	public void clear() {
		pointList.clear();
	}

	public void reCenter() {
		previousCenter=center.getLocation();
		int len=pointList.size();
		int xSum=0, ySum=0;
		for (Point point : pointList) {
			xSum+=point.x;
			ySum+=point.y;
		}
		center.setLocation((int)(xSum/(len+1)),(int)(ySum/(len+1)));
	}

	public boolean isStable() {
		return center.equals(previousCenter);
	}

	public Point getCenter() {
		return center;
	}


	public DImage highlightCluster(DImage image) {
		short [][] red= image.getRedChannel();
		short [][] green= image.getGreenChannel();
		short [][] blue= image.getBlueChannel();
		//debugging purposes 
		short randomR=(short)(Math.random()*256),randomG=(short)(Math.random()*256),randomB=(short)(Math.random()*256);
		for (Point p : pointList) {
			red[p.x][p.y]= randomR;
			blue[p.x][p.y]= randomB;
			green[p.x][p.y]= randomG;
		}

		image.setColorChannels(red, green, blue);
		return image;

	}
}


class Clusters {
	//private static ArrayList<Point> allImgPoints= new ArrayList<>();
	private static Cluster [] clusters;
	private static WhiteList whitePixels;
	public static void loadImage(DImage img) {
		whitePixels= new WhiteList(img);	
	}
	public static void loadClusters(Cluster...clusterz) {
		clusters=clusterz;
	}
	public static void assignPoints() {
		for (Point point: whitePixels.getList()) {
			double minDist=Double.MAX_VALUE;
			int index=-1;
			for (int i=0; i<clusters.length;i++) {
				double currDist=getDistanceBetween(point ,clusters[i].getCenter());
				if(currDist<minDist){
					index=i;
					minDist= currDist; 	
				}
			}
			clusters[index].add(point);
		}
	}

	public static boolean isStable() {
		for (Cluster cluster : clusters) {
			if(!cluster.isStable()) {
				return false;
			}
		}
		return true;
	}

	public static void clearAll() {
		for (Cluster cluster : clusters) {
			cluster.reCenter();
			cluster.clear();
		}
	}

	public static DImage getImage() {
		return whitePixels.toDImage(clusters);
	}
	public static Point [] getInitialLocations(int numClusters) {
		ArrayList<Point> list= whitePixels.getList();
		Point[] arr= new Point[numClusters];
		int skipRate=list.size()/arr.length;
		int index=skipRate;
		for (int i = 0; i < arr.length; i++) {
			arr[i]=list.get(index);
			index+=skipRate;
		}
		return arr;
		
	}

	private static double getDistanceBetween(Point p1, Point p2) {
		return Math.hypot(p1.x-p2.x, p1.y-p2.y);
	}
}
class WhiteList {

	private ArrayList<Point> whiteList;
	private DImage original;
	public WhiteList(DImage img) {
		this.original=img;
		short [][] pixels= img.getBWPixelGrid();
		this.whiteList= new ArrayList<>();

		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[0].length; j++) {
				if(pixels[i][j]==255) {
					this.whiteList.add(new Point(i,j));
				}
			}
		}

	}


	public ArrayList<Point> getList() {
		return whiteList;
	}

	public DImage toDImage( Cluster...clusters ){
		for (Cluster c:clusters) {
			original=c.highlightCluster(original);
		}
		return original;
	}
}

