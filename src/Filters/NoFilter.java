package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class NoFilter implements PixelFilter{

	@Override
	public DImage processImage(DImage img) {
		return img;
	}

}
