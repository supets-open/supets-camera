package cn.jingling.lib.filters.realsize;

import java.io.File;
import java.io.FileNotFoundException;

import cn.jingling.lib.file.ImageFile;
import cn.jingling.lib.file.OtherException;
import cn.jingling.lib.filters.SmoothSkinProcessor;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

public class RSDecolorization extends RSLineFilter{
	
	@Override
	public boolean apply(Context cx, String inPath, String outPath, int[] args) {
		// 产生一张缩略图，大小无特殊要求。
		Bitmap thumb = null;
		try {
			thumb = ImageFile.loadImage(cx, Uri.fromFile(new File(inPath)), 128, 128);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OtherException e) {
			e.printStackTrace();
		}
		if (thumb == null) {
			return false;
		}
		int w = thumb.getWidth();
		int h = thumb.getHeight();
		int[] thumbPixels = new int[w * h];
		
		SmoothSkinProcessor.setupDecolorization(thumbPixels, w, h);
		
		return super.apply(cx, inPath, outPath, args);
	}

	@Override
	protected void applyLine(Context cx, int[] pixels, int line, int height) {
		SmoothSkinProcessor.decolorization(pixels, pixels.length);
	}

	@Override
	protected void releaseLayers() {
		
	}

}
