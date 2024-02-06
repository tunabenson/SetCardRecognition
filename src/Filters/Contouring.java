package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class Contouring implements PixelFilter{

	@Override
	public DImage processImage(DImage img) {
		 img= new ColorMasking().processImage(img);
		 return img;
	}



}
