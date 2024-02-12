package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class ColorMasking implements PixelFilter{
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