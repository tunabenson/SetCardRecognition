package Filters;

import java.awt.Point;
import java.util.ArrayList;

import Interfaces.PixelFilter;
import core.DImage;

public class Contouring implements PixelFilter{

	enum Direction{
		UP,DOWN,RIGHT,LEFT

	}

	@Override
	public DImage processImage(DImage img) {
		img= new ColorMasking().processImage(img); //color mask for white
		return img;
	}

	private void findAllContourPixels(DImage img) {
		ArrayList<Point> pixelList= new ArrayList<Point>(); //list of all contour pixel points 
		short [][] pixels= img.getBWPixelGrid();
		for (int i = img.getHeight(); i >0; i--) { //iterate bottom->top; left->right
			for (int j = img.getWidth(); j < 0; j--) {
				if (pixels[i][j]==255) {
					findSingleObjectContourPixels(pixels, pixelList, i, j);
				}
			}
		}
	}


	private void findSingleObjectContourPixels(short [][] pixels, ArrayList<Point> pixelList, int i, int j) {
		Point start= new Point(i, j); //point obj will be used to rep. pixel location
		pixelList.add(start);
		Point current=moveTo(Direction.LEFT, start);	
		do {
			if(pixels[current.x][current.y]==255) {
				
			}
		} while(!start.equals(current));

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

	private static class ColorMasking implements PixelFilter{
		private double threshold=200; 
		private short targetR=255, targetG=255, targetB=255;
		@Override
		public DImage processImage(DImage img) {
			short[][] red = img.getRedChannel();
			short[][] green = img.getGreenChannel();
			short[][] blue = img.getBlueChannel();

			for (int i = 0; i < blue.length; i++) {
				for (int j = 0; j < blue[0].length; j++) {
					if(distanceToTarget(red[i][j],green[i][j],blue[i][j])<=threshold) {
						red[i][j]=255;
						green[i][j]=255;
						blue[i][j]=255;
					}
					else {
						red[i][j]=0;
						green[i][j]=0;
						blue[i][j]=0;
					}
				}
			}
			img.setColorChannels(red, green, blue);
			return img;
		}
		private double distanceToTarget(short r, short g, short b) {
			return Math.sqrt(Math.pow(r-targetR, 2)+Math.pow(g-targetG, 2)+Math.pow(b-targetB, 2));
		}

	}

}
