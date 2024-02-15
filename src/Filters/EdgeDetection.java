package Filters;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import core.DImage;

public class EdgeDetection {
	private static short threshold=10;
	static ArrayList<Point> detectEdges(DImage img) {
		short [][]pixels= img.getBWPixelGrid();
		ArrayList<Point> edges= new ArrayList<>();
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 1; j < pixels[0].length; j++) {
				if(Math.abs(pixels[i][j-1]-pixels[i][j])>threshold) {
					edges.add(new Point(i,j));
				}
			}

		}
		return edges;
	}
}
