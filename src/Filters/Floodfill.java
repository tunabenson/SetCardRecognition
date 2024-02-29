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

		ArrayList<Blob> bList=findAllCards(img);
		Blob.assignColors(bList,originalImg);
		for (Blob blob : bList) {
			System.out.println(blob.color);
		}
		return Blob.highlight(originalImg, bList);

	}

	private ArrayList<Blob> findAllCards(DImage src){
		ArrayList<Blob> blobList= new ArrayList<>();

		short[][] pixels = src.getBWPixelGrid();
		short[][] pixelsClone = pixels.clone();
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
				Utilities.populateWithSurroundingPixels(pixels, current, queue, (short)255, floodFillCol,b);
			}

			
			//low pass to ensure size fits TODO: use median blob size as filter
			b.findEdges();
//		if(b.size()>500) {
			blobList.add(b);
			starting=getStartingPixel(pixels);
			//b.fixHoles();
//		}
		}
		//int median=Utilities.findMedianSize(blobList);
//		for (int i=0;i<blobList.size();i++) {
//			if(blobList.get(i).size()<500) {
//				blobList.remove(i);
//			}
//		}
		
		
		System.out.println(blobList.size());
		return blobList;

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
