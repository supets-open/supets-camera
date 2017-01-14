package cn.jingling.lib.filters.detection;

import android.graphics.Rect;

public class CVDetectorResults {
	public Human[] humans;
	public int numOfFaces;
	
	public class Human {
		public Rect face;
		public Rect[] eyes;
		public int numOfEyes;
	}
	
}
