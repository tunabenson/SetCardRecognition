package Filters;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import Filters.kmeans.Cluster;
import Interfaces.PixelFilter;
import core.DImage;
import javafx.scene.effect.GaussianBlur;

public class Contouring implements PixelFilter{

	enum Direction{
		UP,DOWN,RIGHT,LEFT

	}

	@Override
	public DImage processImage(DImage img) {
		for(short[] line : Convolution.generateGaussian(1,5)){
			System.out.println(Arrays.toString(line));
		}
		img = new Convolution(Convolution.generateGaussian(1.4,5)).processImage(img);
		img = new ColorReduction(5).processImage(img);
		Cluster[] clusters = new ColorReduction(5).getClusters(img);
		Filters.kmeans.Point val = findClosestToWhite(clusters);
		img = new ColorMasking(30, val.getR(), val.getG(), val.getB()).processImage(img);
		ArrayList<Point> debug = new ArrayList<>();
		ArrayList<Point> list=findAllContourPixels(img,debug);
		short [][] pixels= img.getBWPixelGrid();
		short[][] red = new short[pixels.length][pixels[0].length];
		short[][] green = new short[pixels.length][pixels[0].length];
		short[][] blue = new short[pixels.length][pixels[0].length];
        assert list != null;
        //System.out.println(list.size());
		for (Point pixel: list) {
			red[pixel.x][pixel.y]=255;
		}
		for (Point pixel: debug) {
			try{
				green[pixel.x][pixel.y]=255;
			}catch (Exception e){

			}

		}
		img.setGreenChannel(green);
		img.setBlueChannel(blue);
		img.setRedChannel(red);
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
	private ArrayList<Point> findAllContourPixels(DImage img, ArrayList<Point> debug) {
		ArrayList<Point> pixelList= new ArrayList<Point>(); //list of all contour pixel points 
		short [][] pixels= img.getBWPixelGrid();
		for (int i = pixels.length-1; i >0; i--) { //iterate bottom->top; left->right
			for (int j = pixels[0].length-1; j >0; j--) {
				//System.out.println(i);
				//System.out.println(j);
				if (pixels[i][j]==255) {
					 return findSingleObjectContourPixels(pixels, pixelList,debug, i, j);
					 
				}
			}
		}
		return null;
	}


	private ArrayList<Point> findSingleObjectContourPixels(short [][] pixels, ArrayList<Point> pixelList, ArrayList<Point> debugList, int i, int j) {
		Point start= new Point(i, j); //point obj will be used to rep. pixel location

		pixelList.add(start);
		Direction currDirection=Direction.LEFT;
		Point current=moveTo(currDirection, start);	
		int count=0;
		do {
			if (current.x<0 || current.x>=pixels.length || current.y < 0 || current.y >= pixels[0].length){
				break;
			}
			count++;
			if(pixels[current.x][current.y]==255) {
                assert currDirection != null;
                currDirection= turnTo(0, currDirection);
				current=moveTo(currDirection,current);
				pixelList.add(current);
			}
			else {
                assert currDirection != null;
                currDirection= turnTo(1, currDirection);
				System.out.println(currDirection);
				current=moveTo(currDirection,current);
				debugList.add(current);
			}
			System.out.println(count);
		} while(!start.equals(current));
		return pixelList;
	}

	
	private Point moveTo(Direction d, Point p) {
		if(d==Direction.UP) {
			return new Point((int)p.getX(),(int)p.getY()+1);
		}
		else if(d==Direction.DOWN) {
			return new Point((int)p.getX(),(int)p.getY()-1);
		}
		else if(d==Direction.LEFT) {
			return new Point((int)p.getX()-1,(int)p.getY());
		}
		else {
			return new Point((int)p.getX()+1,(int)p.getY());
		}
	}

	private Direction turnTo(int command, Direction current) { //0 for respective left, 1 for respective right
		switch (current) {
		case UP:
			if(command==0) return Direction.LEFT;
			else return Direction.RIGHT;
					
		case DOWN:
			if(command==0) return Direction.RIGHT;
			else return Direction.LEFT;
			
		case LEFT:
			if(command==0) return Direction.UP;
			else return Direction.DOWN;
			
		case RIGHT:
			if(command==0) return Direction.DOWN;
			else return Direction.UP;

		}
		return null;
	}

	

}
