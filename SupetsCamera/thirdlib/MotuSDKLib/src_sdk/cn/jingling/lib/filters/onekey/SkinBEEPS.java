package cn.jingling.lib.filters.onekey;

import java.util.Arrays;

import android.content.Context;
import android.graphics.Bitmap;
import cn.jingling.lib.filters.OneKeyFilter;
import cn.jingling.lib.filters.SmoothSkinProcessor;

/*====================================================================
 |	BEEPS_
 \===================================================================*/
/*------------------------------------------------------------------*/
public class SkinBEEPS extends OneKeyFilter {

	@Override
	public Bitmap apply(Context cx, Bitmap bm) {
		this.statisticEvent();
		// int w = bm.getWidth();
		// int h = bm.getHeight();
		// 要把这个分3个通道弄进去

		final int width = bm.getWidth();
		final int height = bm.getHeight();

		int[] pixels = new int[width * height];
		int[] srcPixels = new int[width * height];
		int[] skinPixels = new int[width * height];
		int ccc;
		bm.getPixels(pixels, 0, width, 0, 0, width, height);
		bm.getPixels(srcPixels, 0, width, 0, 0, width, height);
		bm.getPixels(skinPixels, 0, width, 0, 0, width, height);
		//先调用一个初始化，setupAll函数
		SmoothSkinProcessor.beepsSetupAll(pixels, width, height);
		
		double[] data = new double[width * height];
		double[] duplicate;
		
		for (int channel = 0; channel != 3; ++channel) 
		{
			for (int i = 0; i != width * height; ++i) 
			{
				data[i] = (double) ((pixels[i] >> (8 * channel)) & 0xFF);
			}

//			for (int i = 0; (i < 1); i++)
//			{
				duplicate = Arrays.copyOf(data, data.length);
				Thread h = new Thread(new BEEPSHorizontalVertical( duplicate, width,
						height));
				Thread v = new Thread(new BEEPSVerticalHorizontal( data, width,
						height));
				h.start();
				v.start();
				try {
					h.join();
					v.join();
				} catch (InterruptedException e) {
				}
			
//			double[] duplicate = Arrays.copyOf(data, data.length);
//			SmoothSkinProcessor.BEEPSHorizontalVertical( duplicate, width, height);
//			SmoothSkinProcessor.BEEPSVerticalHorizontal( data, width, height);
				for (int k = 0, K = data.length; (k < K); k++) 
				{
					data[k] = 0.5F * (data[k] + duplicate[k]);
				}
//			}

			for (int i = 0; i != width * height; ++i) {
				ccc = (int) data[i];
				if (channel == 0) 
				{
					pixels[i] = pixels[i] & 0xFFFFFF00;
					pixels[i] = pixels[i] | ccc;
				}
				if (channel == 1)
				{
					pixels[i] = pixels[i] & 0xFFFF00FF;
					pixels[i] = pixels[i] | (ccc << 8);
				}
				if (channel == 2)
				{
					pixels[i] = pixels[i] & 0xFF00FFFF;
					pixels[i] = pixels[i] | (ccc << 16);
				}
			}
		}
		
		//再把这张图传进去就行了，做红通道的叠加，不过现在不考虑
		//红色通道做蒙版，把数据过滤掉,这个东西可选，因为会影响程度
		SmoothSkinProcessor.beepsOverlay(srcPixels , pixels);
		
		//把那个恢复细节的东西加上
		SmoothSkinProcessor.beepsDetailRecover(srcPixels, pixels);
		SmoothSkinProcessor.skinOverLay(skinPixels, pixels, width, height);
		bm.setPixels(pixels, 0, width, 0, 0, width, height);
		return bm;
	}
//	protected static final double ZETA_3 = 1.2020569031595942853997381615114499907649862923404988817922715553418382057;
//	private static double spatialDecay = 0.02;
//	private static double photometricStandardDeviation = 20.0;
//   private static int iterations = 1;
//	private static int rangeFilter = 0;
}
//
//class BEEPSHorizontalVertical implements Runnable
//{ 
//	private double[] data;
//	private int height;
//	private int width;
//	protected BEEPSHorizontalVertical(final double[] data, final int width,
//			final int height) {
//		this.data = data;
//		this.width = width;
//		this.height = height;
//	}
//	public void run() 
//	{
//		//加包名字
//		SmoothSkinProcessor.BEEPSHorizontalVertical(data, width, height);
//	} 
//} 
//
//class BEEPSVerticalHorizontal implements Runnable
//{    	
//    private double[] data;
//    private int height;
//    private int width;
//    protected BEEPSVerticalHorizontal(final double[] data, final int width,
//		final int height) 
//    {
//	this.data = data;
//	this.width = width;
//	this.height = height;
//    }
//	public void run() 
//	{	
//		//加包的名字
//		SmoothSkinProcessor.BEEPSVerticalHorizontal(data, width, height);
//	} 
//}
