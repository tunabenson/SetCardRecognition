package core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Blob {
	private ArrayList<Point> blob;
	private ArrayList<Point> edges;
	//private Point [] corners;
	private short cornerR;
	private short cornerG;
	private short cornerB;
	public String color;
	public Blob() {
		this.blob= new ArrayList<>();
		this.edges= new ArrayList<>();
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

	public void addEdge(Point p) {
		edges.add(p);
	}

	public void findEdges() {		

		edges = ConvexHulls.convexHull(blob); //finds prominent points
		//		System.out.println(edges);
		for (int i = edges.size()-2; i >=0; i--) {
			Point p1=edges.get(i+1);
			Point p2=edges.get(i);
			double [] fx=getFx(p1, p2);
			if(Double.isInfinite(fx[0])) {
				if(p1.y>p2.y) {  
					for (int j = p1.y; j >=p2.y; j--) {
						edges.add(new Point((int)fx[1],j));
						//edges.add(new Point(j,(int)fx[1]));
					}
				}
				else {
					for (int j = p2.y; j >= p1.y; j--) {
						edges.add(new Point((int)fx[1],j));
						//edges.add(new Point(j,(int)fx[1]));
					}
				}
			}

			if(fx[0]==0) {
				if(p1.x>p2.x) {  
					for (int j = p1.x; j >=p2.x; j--) {
						edges.add(new Point(j,p1.y));
						//edges.add(new Point(j,(int)fx[1]));
					}
				}
				else {
					for (int j = p2.x; j >= p1.x; j--) {
						edges.add(new Point(j,p1.y));
						//edges.add(new Point(j,(int)fx[1]));
					}
				}
			}

			else {
				if(p1.x>p2.x) {
					for (int j = p1.x; j >=p2.x; j--) {
						//Point temp=new Point();
						//temp.setLocation(j,(j*fx[0])+fx[1]);						
						//.add(temp);
						edges.add(new Point(j,(int)((j*fx[0])+fx[1])));
					}
				}
				else {
					for (int j = p2.x; j >=p1.x; j--) {
						//						Point temp=new Point();
						//						temp.setLocation(j,(j*fx[0])+fx[1]);						
						//						edges.add(temp);
						edges.add(new Point(j,(int)((j*fx[0])+fx[1])));
					}
				}
				//				for (int j = p1.x+1; j < p2.x; j++) {
				//					edges.add(new Poifgfnt(j,(int)((j*fx[0])+fx[1])));
				//					//edges.add(new Point((int)((j*fx[0])+fx[1]),j));
				//
				//				}
			}
		}
		
	}

	public void fixHoles(DImage proc, DImage org) {
		short [][] green= org.getGreenChannel();
		short [][] blue= org.getBlueChannel();
		short [][] red= org.getRedChannel();
		short [][] greenP= proc.getGreenChannel();
		short [][] blueP= proc.getBlueChannel();
		short [][] redP= proc.getRedChannel();
		for (Point point : edges) {
			for (Point point1 : edges) {
				if(point.x==point1.x) {
					int start,end;
					if(point.y<point1.y) {
						start=point.y;
						end=point1.y;
					}
					else {
						end=point.y;
						start=point1.y;
					}
					
					for (int i = start; i < end; i++) {
						if(greenP[point.x][i]==0 && blueP[point.x][i]==0  && redP[point.x][i]==0) {
							Utilities.floodFill(proc, point.x,i, (short)0, (short)0, (short)0);
						}
					}
					return;
				}
				
			}
		}
	}
	
	

	private void highlightCorner(short [][] red, short[][] blue, short[][] green) {



		for (Point p: edges) {
			for (int i =p.x-3; i < p.x+3; i++) {
				for (int j =p.y-3; j < p.y+3; j++) {
					if(i>0 && i<red.length && j>0 && j<red[0].length) {
						red[i][j]=cornerR;
						blue[i][j]=cornerB;
						green[i][j]=cornerG;
					}
				}
			}
		}
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



	public static void assignColors(ArrayList<Blob> bList, DImage original) {
		short [][] blueChannel=original.getBlueChannel();
		short [][] redChannel=original.getRedChannel();
		short [][] greenChannel=original.getGreenChannel();
		short [] red= new short [] {255,69,0};
		short [] green= new short [] {0,128,0};
		short [] purple= new short [] {148,0,211};

		for (Blob blob : bList) {
			int index=-1;
			ArrayList<Point> blobz=blob.get();
			double minDist=Double.MAX_VALUE;
			for (Point p: blobz) {
				short r=redChannel[p.x][p.y];
				short g=greenChannel[p.x][p.y];
				short b=blueChannel[p.x][p.y];
				double [] dists= { Utilities.distance(red[0],red[1],red[2],r,g,b)
						,Utilities.distance(purple[0],purple[1],purple[2],r,g,b)
						,Utilities.distance(green[0],green[1],green[2],r,g,b)};
				for (int i = 0; i < dists.length; i++) {
					System.out.print(dists[i]+"    ");
					if(dists[i]<minDist) {
						minDist=dists[i];
						index=i;
					}
				}
				System.out.println("\n");
			}
			switch (index) {
			case 0: blob.color="red";break;
			case 1: blob.color="purple";break;
			case 2: blob.color="green";break;
			}


		}
	}
}



