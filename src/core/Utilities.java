package core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import Filters.kmeans.Cluster;

public class Utilities {

	public static Filters.kmeans.Point findClosestTo(Cluster[] clusters, int r, int g, int b) {
		Filters.kmeans.Point color = new Filters.kmeans.Point((short)r,(short)g,(short)b);
		Filters.kmeans.Point closeColor = new Filters.kmeans.Point((short)0, (short)0, (short)0);
		double minDist = Double.MAX_VALUE;
		for(int i = 0; i < clusters.length; i++){
			double currDist=clusters[i].getCenter().distanceTo(color);
			if(currDist < minDist){
				minDist = currDist;
				closeColor = clusters[i].getCenter();
			}
		}
		return closeColor;
	}


	public static double distance(short x1,short y1,short z1, short x2, short y2, short z2) {
		int xDiff=(x1-x2)*(x1-x2);
		int yDiff=(y1-y2)*(y1-y2);
		int zDiff=(z1-z2)*(z1-z2);
		return Math.sqrt(xDiff+yDiff+zDiff);
	}


	public static int findMedianSize(ArrayList<Blob> blobList) {
		int [] sizes= new int [blobList.size()];
		for (int i = 0; i < sizes.length; i++) {
			sizes[i]=blobList.get(i).size();
		}
		Arrays.sort(sizes);
		int med=0;
		if(sizes.length%2==0) {
			med=sizes[sizes.length/2]+sizes[(sizes.length/2)+1];
			med/=2;
		}
		else {
			med=sizes[sizes.length/2];
		}
		return med;

	}


	public static void floodFill(DImage proc, int x, int y, short r, short g, short b) {
		// TODO Auto-generated method stub

	}


	public static void populateWithSurroundingPixels(short[][] pixels, Point current, ArrayList<Point> queue, short oldColor, short newColor, Blob b) {
		//Check four directions around pixel and see if they are white
		Point up = new Point(current.x -1, current.y);
		Point down = new Point(current.x + 1, current.y);
		Point left = new Point(current.x , current.y-1);
		Point right = new Point(current.x , current.y + 1);
		if(isPixelValidForFloodFill(pixels,up, oldColor, newColor)){
			pixels[up.x][up.y] = newColor;
			b.add(up);
			queue.add(up);
		}
		if(isPixelValidForFloodFill(pixels,down, oldColor, newColor)){
			pixels[down.x][down.y] = newColor;
			b.add(down);
			queue.add(down);
		}
		if(isPixelValidForFloodFill(pixels,left, oldColor, newColor)){
			pixels[left.x][left.y] = newColor;
			b.add(left);
			queue.add(left);
		}
		if(isPixelValidForFloodFill(pixels,right, oldColor, newColor)){
			pixels[right.x][right.y] = newColor;
			b.add(right);
			queue.add(right);
		}
	}


	private static boolean isPixelValidForFloodFill(short[][] pixels, Point pixel, short oldColor, short newColor){
		int x = pixel.x;
		int y = pixel.y;
		if(x < 0 || x >= pixels.length || y < 0 || y >= pixels[0].length || pixels[x][y] != oldColor || pixels[x][y] == newColor){
			return false;
		}

		return true;
	}

}
