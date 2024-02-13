package Filters;

import Filters.kmeans.Cluster;
import Interfaces.PixelFilter;
import core.DImage;
import core.Utilities;
@Deprecated
public class PreProcess implements PixelFilter{
	private DImage source;
	@Override
	public DImage processImage(DImage img) {
		this.source=img;//intact original image
		img= new DImage(img); //this will be our altered/ processed image
		img=runPreProcess(img);
		return img;
		
	}
	private DImage runPreProcess(DImage img) {
		ColorReduction reduce= new ColorReduction(15);
		img=reduce.processImage(img);
		Cluster [] arr=reduce.getClusters(img);
		ColorMasking mask= new ColorMasking();
		maskBestFit(img, mask, arr, 255, 0, 0);
		maskBestFit(img, mask, arr, 0, 255, 0);
		maskBestFit(img, mask, arr, 95,0, 160);
		//img = new ColorReduction(5).processImage(img);
		GuassianBlur b=new GuassianBlur();
		img= b.processImage(img);	
		img = new ColorReduction(2).processImage(img);
		
	
		Cluster[] clusters =new ColorReduction(2).getClusters(img);
////		//mask all centers components of card 
		Filters.kmeans.Point val = Utilities.findClosestTo(clusters,255,255,255);
		new ColorMasking(20, val.getR(), val.getG(), val.getB()).processImage(img);
//		//img=b.processImage(img);
		return img;
		//new ColorMasking(20, (short)255, (short)255, (short)255).processImage(img);
		//return img;
	}
	
	
	private void maskBestFit(DImage src, ColorMasking m, Cluster [] arr, int r, int g, int b) {
		Filters.kmeans.Point point= Utilities.findClosestTo(arr,r, g, b);
		m.processImageToFitPrevious(src, point.getR(), point.getG(), point.getB());
	}

}
