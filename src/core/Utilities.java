package core;

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
}
