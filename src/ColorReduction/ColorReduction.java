package ColorReduction;


import java.util.ArrayList;
import Interfaces.PixelFilter;
import core.DImage;

public class ColorReduction implements PixelFilter{
	private int k=2;
	
	@Override
	public DImage processImage(DImage img) {
		Clusters.loadImage(img);
		Cluster [] list= new Cluster[k];
		for (int i = 0; i < k; i++) {
			short [] color= getRandomColor(img);
			list[i]= new Cluster(color[0], color[1],color[2]);
		}
		Clusters.loadClusters(list);

		while(!Clusters.isStable()) {
			Clusters.assignPoints();
			Clusters.clearAll();
		}
		Clusters.assignPoints();
		return Clusters.getImage();

	}
	
	
	private short [] getRandomColor(DImage image) {
		short [] color= new short[3];
		int i=(int)(Math.random()*image.getHeight());
		int j=(int)(Math.random()*image.getWidth());
		color[0]=image.getRedChannel()[i][j];
		color[1]=image.getGreenChannel()[i][j];
		color[2]=image.getBlueChannel()[i][j];
		return color;
		
		
	}

}
class Clusters {
	private static ArrayList<ColorPoint> allImgPoints= new ArrayList<>();
	private static Cluster [] clusters;
	private static PointImage image;
	public static void loadImage(DImage img) {
		image= new PointImage(img);
		allImgPoints=image.toList();
	}
	public static void loadClusters(Cluster...clusterz) {
		clusters=clusterz;
	}
	public static void assignPoints() {
		for (ColorPoint point: allImgPoints) {
			double minDist=Double.MAX_VALUE;
			int index=-1;
			for (int i=0; i<clusters.length;i++) {
				double currDist=point.getDistanceFrom(clusters[i].getCenter());
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
		for (Cluster cluster : clusters) {
			cluster.setPointsToColor();
		}
		return image.toDImage();
	}







}


class Cluster {
	private ColorPoint previousCenter;
	private ColorPoint center; 
	private ArrayList<ColorPoint> pointList;

	public Cluster(short r, short g, short b) {
		previousCenter= new ColorPoint((short)-1,(short) -1,(short) -1);
		center= new ColorPoint(r, g, b);
		pointList= new ArrayList<>();
	}

	public void add(ColorPoint p) {
		pointList.add(p);
	}

	public void clear() {
		pointList.clear();
	}

	public void reCenter() {
		previousCenter=center.getCopy();
		int len=pointList.size();
		int rSum=0, gSum=0, bSum=0;
		for (ColorPoint colorPoint : pointList) {
			rSum+=colorPoint.getRed();
			gSum+=colorPoint.getGreen();
			bSum+=colorPoint.getBlue();
		}
		center.setRed((short)(rSum/(len+1)));
		center.setGreen((short)(gSum/(len+1)));
		center.setBlue((short)(bSum/(len+1)));

	}

	public boolean isStable() {
		return center.equals(previousCenter);
	}

	public ColorPoint getCenter() {
		return center;
	}


	public void setPointsToColor() {
		for (ColorPoint colorPoint : pointList) {
			colorPoint.setBlue(center.getBlue());
			colorPoint.setGreen(center.getGreen());
			colorPoint.setRed(center.getRed());

		}
	}

}

class PointImage {
	
	private ColorPoint[][] img;
	
	public PointImage(DImage img) {
		short[][] red = img.getRedChannel();
		short[][] green = img.getGreenChannel();
		short[][] blue = img.getBlueChannel();
		this.img= new ColorPoint[red.length][red[0].length];
		
		for (int i = 0; i < blue.length; i++) {
			for (int j = 0; j < blue[0].length; j++) {
				this.img[i][j]= new ColorPoint(red[i][j],green[i][j], blue[i][j]);
			}
		}

	}

	public DImage toDImage() {
		int height=img.length;
		int width=img[0].length;
		short[][] red = new short[height][width];
		short[][] green = new short [height][width];
		short[][] blue = new short [height][width];

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				ColorPoint p= img[i][j];
				red[i][j]=p.getRed();
				green[i][j]=p.getGreen();
				blue[i][j]=p.getBlue();
			}
		}
		DImage img= new DImage(width, height);
		img.setColorChannels(red, green, blue);
		return img;
	}

	public ArrayList<ColorPoint> toList() {
		ArrayList<ColorPoint> points= new ArrayList<>();
		for (ColorPoint[] arr : img) {
			for (ColorPoint colorPoint : arr) {
				points.add(colorPoint);
			}
		}
		return points;
	}

}

class ColorPoint {
	private short red;
	private short green;
	private short blue;
	public ColorPoint(short r, short g, short b) {
		red=r;
		green=g;
		blue=b;

	}
	public short getRed() {
		return red;
	}
	public void setRed(short red) {
		this.red = red;
	}
	public short getGreen() {
		return green;
	}
	public void setGreen(short green) {
		this.green = green;
	}
	public short getBlue() {
		return blue;
	}
	public void setBlue(short blue) {
		this.blue = blue;
	}


	public boolean equals(ColorPoint point) {
		return this.getBlue()==point.getBlue() && this.getGreen()==point.getGreen() && this.getRed()==point.getRed();
	}
	
	public ColorPoint getCopy() {
		return new ColorPoint(this.red, this.green, this.blue);
	}
	public double getDistanceFrom(ColorPoint center) {
		return Math.sqrt(Math.pow(center.getRed()-red, 2)+Math.pow(center.getGreen()-green, 2)+Math.pow(center.getBlue()-blue, 2));
	}
}


