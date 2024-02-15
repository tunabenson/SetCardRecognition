package core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Blob {
	private ArrayList<Point> blob;
	private ArrayList<Point> edges;
	private Point [] corners;
	private short cornerR;
	private short cornerG;
	private short cornerB;
	public Blob() {
		this.blob= new ArrayList<>();
		cornerR=(short)(Math.random()*256);
		cornerG=(short)(Math.random()*256);
		cornerB=(short)(Math.random()*256);
	}
	public Blob(ArrayList<Point> blob) {
		this.blob= blob;
	}
	public void add(Point p) {
		// TODO Auto-generated method stub
		blob.add(p);

	}
	public void addAll(ArrayList<Point> pList) {
		// TODO Auto-generated method stub
		blob.addAll(pList);

	}
	public ArrayList<Point> get(){
		return blob;
	}

	public int size() {
		return blob.size();
	}

	public void findCorners() {

	}



	public void fillAll() {

	}

	public static DImage highlight(DImage src, ArrayList<Blob> bList) {
		short [][] red= src.getRedChannel();
		short [][] blue= src.getBlueChannel();
		short [][] green= src.getGreenChannel();

		short [][] newRed= new short [red.length][red[0].length];
		short [][] newBlue= new short [red.length][red[0].length];
		short [][] newGreen= new short [red.length][red[0].length];

		for (Blob blob:bList) {
			for(Point p: blob.get()) {
				newRed[p.x][p.y]=red[p.x][p.y];
				newGreen[p.x][p.y]=green[p.x][p.y]; 
				newBlue[p.x][p.y]=blue[p.x][p.y];
			}
			blob.highlightCorner(newRed, newBlue, newGreen);
		}


		src.setColorChannels(newRed, newGreen, newBlue);
		return src;
	}

	public void findEdges() {		
	
		edges = ConvexHulls.convexHull(blob); //finds prominent points
		//for (Point p:edges) {
			//if(p.x>1000 || p.y>1000) {
				//System.out.println(p);
			//}
		//}

		for (int i = 1; i < edges.size(); i++) {
			Point p1=edges.get(i-1);
			Point p2=edges.get(i);
			double [] fx=getFx(p1, p2);
			if(Double.isInfinite(fx[0])) {
				for (int j = p1.y; j < p2.y; j+=3) {
					//System.out.println("infislope: "+fx[1]);
					edges.add(new Point((int)fx[1],j));
				}
			}
			
			else {
				for (int j = p1.x+1; j < p2.x; j+=3) {
					//System.out.println(p1);
					//System.out.println(new Point(j,(int)((j*fx[0])+fx[1])));
					edges.add(new Point(j,(int)((j*fx[0])+fx[1])));
					
				}
			}
		}
		System.out.println(edges.size()+"hello");
	}


		private void highlightCorner(short [][] red, short[][] blue, short[][] green) {
			



			//	ArrayList<Point> allPoints= new ArrayList<>();
			//	for (int i = 1; i < edges.length-1; i++) {
			//		Point p1= edges[i-1];
			//		Point p2= edges[i];
			//		Point p3= edges[i+1];
			//		double slope;
			//		if((slope=isCollinear(p1,p2,p3))!=Double.NaN) {
			//			if(slope==Double.NEGATIVE_INFINITY) {
			//				for (int j = p1.y; j < p3.y; j+=3) {
			//					allPoints.add(new Point(p1.x, j));
			//				}
			//			}
			//			
			//			else {
			//				double b= p1.y-(slope*p1.x);
			//				for (int j = p1.x; j <= p3.x; j+=3) {
			//					allPoints.add(new Point(j, (int)((slope*j)+b)));
			//				}
			//			}
			//		}
			//		allPoints.add(p1);
			//	}

			for (Point p: edges) {
				for (int i =p.x-1; i < p.x+1; i++) {
					for (int j =p.y-1; j < p.y+1; j++) {
						if(i>0 && i<red.length && j>0 && j<red[0].length) {
							red[i][j]=cornerR;
							blue[i][j]=cornerB;
							green[i][j]=cornerG;
						}
					}
				}
			}

			/*Point p=blob.get(0);
		for (int i = p.x-5; i < p.x+5; i++) {
			for (int j =p.y-5; j < p.y+5; j++) {
				red[i][j]=cornerR;
				blue[i][j]=cornerG;
				green[i][j]=cornerB;
			}
		}*/
		}
		private double [] getFx(Point p1, Point p2) {
			if(p1.x-p2.x==0) { // edge case to deal with 
				return new double []{Double.NEGATIVE_INFINITY, p1.x}; // flag vertical line
			}
			if(p1.y==p2.y) {
				return new double[] {0, p1.y};
			}
				double m=(double)(p1.y-p2.y)/(p1.x-p2.x);
				double b= p1.y-(m*p1.x);
				return new double [] {m,b};
			
		}



	}

