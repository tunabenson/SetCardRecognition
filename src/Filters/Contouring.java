package Filters;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import Interfaces.PixelFilter;
import core.DImage;

public class Contouring implements PixelFilter{

	enum Direction{
		UP,DOWN,RIGHT,LEFT

	}

	@Override
	public DImage processImage(DImage img) {
		DImage newImage= new ColorMasking().processImage(img); //color mask for white
		ArrayList<Point> list=findAllContourPixels(newImage);
		short [][] pixels= img.getBWPixelGrid();
		for (Point pixel: list) {
			pixels[pixel.x][pixel.y]=255;
		}
		img.setPixels(pixels);
		return img;
	}

	private ArrayList<Point> findAllContourPixels(DImage img) {
		ArrayList<Point> pixelList= new ArrayList<Point>(); //list of all contour pixel points 
		short [][] pixels= img.getBWPixelGrid();
		for (int i = pixels.length-1; i >0; i--) { //iterate bottom->top; left->right
			for (int j = pixels[0].length-1; j >0; j--) {
				//System.out.println(i);
				//System.out.println(j);
				if (pixels[i][j]==255) {
					System.out.println("made it");
					 return findSingleObjectContourPixels(pixels, pixelList, i, j);
					 
				}
			}
		}
		return null;
	}


	private ArrayList<Point> findSingleObjectContourPixels(short [][] pixels, ArrayList<Point> pixelList, int i, int j) {
		Point start= new Point(i, j); //point obj will be used to rep. pixel location
		pixelList.add(start);
		Direction currDirection=Direction.LEFT;
		Point current=moveTo(currDirection, start);	
		int count=0;
		do {
			count++;
			if(pixels[current.x][current.y]==255) {
				currDirection= turnTo(0, currDirection);
				System.out.println(currDirection);
				current=moveTo(currDirection,current);
				pixelList.add(current);
			}
			else {
				currDirection= turnTo(1, currDirection);
				current=moveTo(currDirection,current);
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
