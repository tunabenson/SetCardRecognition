package Filters;


import Filters.kmeans.Cluster;
import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.Blob;
import core.DImage;
import core.Utilities;

import java.awt.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.logging.Filter;


public class Floodfill implements PixelFilter {

	int x, y;

	@Override
	public DImage processImage(DImage img) {
		DImage originalImg= new DImage(img);

//		img=new PreProcess().processImage(img);
		img = new ColorReduction(4).processImage(img);
		for (int i = 0; i < 15; i++) {
		img= new GuassianBlur().processImage(img);
		}
		img = new ColorReduction(2).processImage(img);
		
		Cluster[] clusters = new ColorReduction(2).getClusters(img);
		//mask all centers components of card 
		Filters.kmeans.Point val = Utilities.findClosestTo(clusters,255,255,255);
		
		new ColorMasking(50, val.getR(), val.getG(), val.getB()).processImage(img);
		//return img;
		
		return Blob.highlight(originalImg, findAllCards(img));
		
	}
	
	private ArrayList<Blob> findAllCards(DImage src){
		ArrayList<Blob> blobList= new ArrayList<>();
		
		short[][] pixels = src.getBWPixelGrid();
		short floodFillCol = 0;
		//Get the first white pixel
		Point starting = getStartingPixel(pixels);
		
		while(starting!=null) {
			//Stack for storing surrounding pixels of the same color
			ArrayList<java.awt.Point> queue = new ArrayList<java.awt.Point>();
			queue.add(starting);
			Blob b= new Blob();
			b.add(starting);
			
			while(!queue.isEmpty()){ //find singular object
				Point current = queue.remove(0);
				int posX = current.x;
				int posY = current.y;;
				populateWithSurroundingPixels(pixels, current, queue, (short)255, floodFillCol,b);
			}
			if (b.size()>500) {
			b.findCorners();
			blobList.add(b);
			}
			starting=getStartingPixel(pixels);
		}
		System.out.println(blobList.size());
		return blobList;
		
	}
	


	private void populateWithSurroundingPixels(short[][] pixels, Point current, ArrayList<Point> queue, short oldColor, short newColor, Blob b) {
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

		return null;
	}
}
