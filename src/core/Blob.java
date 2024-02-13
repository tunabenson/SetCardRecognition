package core;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Blob {
	private ArrayList<Point> blob;
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
		Point p1=blob.get(0);// top right corner
		Point p2=blob.get(1);// top left corner
		Point p3=blob.get(2);// bottom right corner
		Point p4=blob.get(3);// bottom left corner
		
		for (int i = 0; i < blob.size(); i++) {
			Point temp=blob.get(i);
			if(p1.y<temp.y && p1.x>temp.x) p1=temp;
			if(p2.y>temp.y && p2.x>temp.x) p2=temp;
			if(p3.y<temp.y && p3.x<temp.x) p3=temp;
			if(p4.y>temp.y && p4.x<temp.x) p4=temp;
		}
		corners= new Point[]{p1,p2,p3,p4};
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

	private void highlightCorner(short [][] red, short[][] blue, short[][] green) {
		List<int[]> edges = ConvexHulls.convexHull(blob);
		for (int [] point: edges) {
			for (int i = point[0]-5; i < point[0]+5; i++) {
				for (int j =point[1]-5; j < point[1]+5; j++) {
					if(i>0 && i<red.length && j>0 && j<red[0].length) {
						red[i][j]=cornerR;
						blue[i][j]=cornerB;
						green[i][j]=cornerG;
					}
				}
			}
		}
	}
}
