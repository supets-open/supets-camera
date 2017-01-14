package cn.jingling.lib.advanceedit.brush;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Bitmap.Config;
import android.graphics.Shader.TileMode;
import android.widget.ImageView;

/**
 * @Copyright(C)2012,  Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: MosaicState.java Created On 2012-10-16
 * @Author:yangding
 * @Description:马赛克画笔
 * 
 */
public class MosaicState extends DrawState {
	
	private static final int MOSAIC_RANGE = 12;
	
	/** 马赛克画笔构造函数。
	 * @param groundBitamp 生成马赛克所用的图片。马赛克是由这张图做像素块混淆后生成的。
	 * @param pathBitmap 绘画所用的底图。画笔绘制的涂鸦就是画在这张图上的。
	 */
	public MosaicState(Bitmap groundBitamp,Bitmap pathBitmap)
	{
		super(pathBitmap);
		Bitmap mMosaicBitmap= groundBitamp.copy(Config.ARGB_8888, true);
		
		int w = mMosaicBitmap.getWidth();
		int h = mMosaicBitmap.getHeight();
		int[] pixels = new int[w * h];
		mMosaicBitmap.getPixels(pixels, 0, w, 0, 0, w, h);
		pixels = mosaic(pixels,w,h,MOSAIC_RANGE);
		mMosaicBitmap.setPixels(pixels, 0, w, 0, 0, w, h);
		
		BitmapShader bitmapShader = new BitmapShader(mMosaicBitmap,TileMode.CLAMP,TileMode.CLAMP);
		paint.setShader(bitmapShader);
	}
	
	
	/** 用于生产马赛克图片的方法
	 * @param pixels 原图pixel
	 * @param w 
	 * @param h
	 * @param range
	 * @return 生成马赛克图片的pixel
	 */
	private int[] mosaic(int[] pixels,int w,int h,int range)
	{
		int totalLine = pixels.length/w-1;
		int totalColume = pixels.length/h-1;

		int lineNumber = 0;
		int columeNumber = 0;
		
		int realRangeLine;
		int realRangeColume;

		while(lineNumber < totalLine)
		{
			if((lineNumber + range -1) <= totalLine)
			{
				realRangeLine = range-1;
			}
			else
			{
				realRangeLine = totalLine - lineNumber;
			}
			
			while(columeNumber < totalColume)
			{
				if((columeNumber + range -1) <= totalColume)
				{
					realRangeColume = range-1;
				}
				else
				{
					realRangeColume = totalColume - columeNumber;
				}
				
				int a = 0;
				int r = 0;
				int g = 0;
				int b = 0;
				for(int i=0;i<=realRangeLine;i++)
				{
					for(int j = 0 ;j<=realRangeColume;j++)
					{
						int index = (lineNumber+i)*w+columeNumber+j;
						a += (pixels[index] >> 24) & 0xFF;
						r += (pixels[index] >> 16) & 0xFF;
						g += (pixels[index] >> 8) & 0xFF;
						b += (pixels[index]) & 0xFF;
					}
				}
				int total = (realRangeLine+1)*(realRangeColume+1);
				a = a/total;
				r = r/total;
				g = g/total;
				b = b/total;
				for(int i=0;i<=realRangeLine;i++)
				{
					for(int j = 0 ;j<=realRangeColume;j++)
					{
						int newColor = (a << 24) | (r << 16) | (g << 8) | b;
						pixels[(lineNumber+i)*w+columeNumber+j] = newColor;
					}
				}
		
				columeNumber = columeNumber + realRangeColume+1;

			}
			
			
			lineNumber = lineNumber + realRangeLine+1;
			
			columeNumber = 0;
		}
		
		return pixels;
	}

}
