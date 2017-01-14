package cn.jingling.lib.filters.partial;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import cn.jingling.lib.file.CopyOfLog;
import cn.jingling.lib.filters.CMTProcessor;
import android.view.MotionEvent;
import android.widget.ImageView;
import cn.jingling.lib.filters.PartialFilter;

public class WhelkRemoveTest2 extends PartialFilter{
	protected String TAG = "PartialSkinSmoothEffect_OK";
	private int mRADIUS = 20;
	private int mMaxRadius=100;
	private int mMinRadius=10;
	private int FSIZE=20;
	private Bitmap mAssistBitmap;
	boolean isFirst = true;
	private ImageView mZoomView, mAssistView;

	public void setup(Context cx, Bitmap bm) {
		super.setup(cx, bm);
//		mAssistView = new ImageView(cx);
//		mAssistView = (ImageView) v;
//		mZoomView = new ImageView(cx);
//		mZoomView = (ImageView) v1;
		//mOriginalBitmap = bm.copy(bm.getConfig(), true);
//		final CVDetector detector = new CVDetector(cx);
//		CVDetectorResults mDetectionResult = new CVDetectorResults();
//		Mat mat = new Mat();
//		Utils.bitmapToMat(bm, mat);
//		detector.setEyeDetect(false);
//		mDetectionResult = detector.drawFaceRect(mat);
		mRADIUS = bm.getHeight() / 20;
		mMaxRadius= Math.min(bm.getWidth(), bm.getHeight())/10;
//		if (mDetectionResult.numOfFaces != 0)
//			mRADIUS = (mDetectionResult.humans[0].face.right - mDetectionResult.humans[0].face.left) / 15;
	}

//	public Bitmap apply(Bitmap bm, View v, MotionEvent event) {
//		// TODO Auto-generated method stub
//		switch (event.getActionMasked()) {
//		case MotionEvent.ACTION_DOWN:
//			drawCircle(v, event, bm);
//			break;
//		case MotionEvent.ACTION_MOVE:
//			drawCircle(v, event, bm);
//			break;
//		case MotionEvent.ACTION_UP:
//			whelkRemoveByThreshold(v, event, bm);
//			break;
//		default:
//			break;
//		}
//		return bm;
//	}
	
	public void setRelativeRadius(Bitmap bm, int degree) {
		mRADIUS = degree * (mMaxRadius - mMinRadius) / 100 + mMinRadius;
		if (mRADIUS < mMinRadius) {
			mRADIUS = mMinRadius;
		} else if (mRADIUS > mMaxRadius) {
			mRADIUS = mMaxRadius;
		}
	}
	
	public int getRelativeRadius() {
		return (mRADIUS - mMinRadius) * 100 / (mMaxRadius - mMinRadius);
	}
	

	/**
	 * @param selection
	 *            �𻯾���
	 * @param x
	 *            �����������x
	 * @param y
	 *            �����������y
	 * @param r
	 *            ����뾶
	 * @param w
	 *            ������
	 * @param h
	 *            ����߶�
	 * @param featherSize
	 *            �𻯳ߴ�
	 */
	private void selectRound(int[] selection, int x, int y, int r, int w,
			int h, int featherSize) {
		final int f = featherSize;
		int r2 = r * r;
		int fr2 = (r - f) * (r - f);

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int d2 = (i - x) * (i - x) + (j - y) * (j - y);
				if (d2 < fr2) {
					selection[i * w + j] = 0;
				} else if (d2 < r2) {
					int d = (int) Math.sqrt(d2);
					selection[i * w + j] = 255 - 255 * (r - d) / f;
				} else {
					selection[i * w + j] = 255;
				}
			}
		}

	}

//	private void whelkRemoveByThreshold(View v, MotionEvent event, Bitmap bm){
//		int x = getBitmapX(v, (int) event.getX());
//		int y = getBitmapY(v, (int) event.getY());
//		int w = bm.getWidth();
//		int h = bm.getHeight();
//		int tw = mRADIUS;
//		int th = mRADIUS;
//		int x0 = x - tw / 2;
//		if (x0 < 0)
//			x0 = 0;
//		int y0 = y - th / 2;
//		if (y0 < 0)
//			y = 0;
//		int x00 = x + tw / 2;
//		if (x00 > w)
//			x00 = w - 1;
//		int y00 = y + th / 2;
//		if (y00 > h)
//			y00 = h - 1;
//		//���ֵ
//		int[] origPixels = new int[w * h];
//		bm.getPixels(origPixels, 0, w, 0, 0, w, h);
//		int mean=0;
//		int i,j;
//		for(i=0;i<th;i++){
//			for(j=0;j<tw;j++){
//				mean=mean+(origPixels[x0+j+(y0+i)*w]& 0xff);
//			}
//		}
//		mean = mean/(tw*th);
//		int highThres = mean+16;
//		int lowThres = mean-16;
//		
//		//��ֱ��ͼ
//		int[] hist = new int[16];
//		int l=0;
//		int histMax =0, indexMax = 0;
//		for(l=0;l<16;l++){
//			hist[l]=0;
//		}
//		for(i=0;i<th;i++){
//			for(j=0;j<tw;j++){
//				int index = 0;
//				index = (origPixels[x0+j+(y0+i)*w]& 0xff)/16;
//				hist[index]= hist[index]+1;
//			}
//		}
//		for (l =0;l<16;l++){
//			if(l == 0){
//			   histMax = hist[0];
//			   indexMax = 0;
//			}
//			else if(histMax < hist[l]){
//				histMax = hist[l];
//				indexMax = l;
//			}	
//		}
//		highThres = (indexMax+1)*16+16;
//		lowThres =  (indexMax+1)*16-16;
//		
//		boolean find=false;
//		int tempPixel = 0;
//		for(i=0;i<th;i++){
//			for(j=0;j<tw;j++){
//				tempPixel = origPixels[x0+i+(y0+j)*w]& 0xff;
//				if ((origPixels[x0+i+(y0+j)*w]& 0xff) < lowThres || (origPixels[x0+i+(y0+j)*w]& 0xff) > highThres){
//					origPixels[x0+i+(y0+j)*w]=0;
//					int r = Math.min(Math.min(i,th-i-1), Math.min(j, tw-j-1))+1;
//					for(l=0; l<r;l++){
//						for(int m = y0+i-l; m<y0+i+l; m++){
//							for(int n = x0+i-l; n<x0+i+l; n++){
//								if((bm.getPixel(n, m)& 0xff) > lowThres & (bm.getPixel(n, m)& 0xff) < highThres){
//									origPixels[(x0+j)+(y0+i)*w] = bm.getPixel(n, m);
//									find = true;
//								}
//								if(find == true)
//									break;
//							}
//							if(find == true)
//								break;
//						}
//						if(find == true)
//							break;
//					}
//					
//					
//				}
//			}
//		}
//		
//		bm.setPixels(origPixels, 0, w, 0, 0, w, h);
//	}
	/**
	 * ���÷���ͼ�·���ȥ��Ȧ���Ķ���
	 * 
	 * @param v
	 *            ��ͼ
	 * @param event
	 *            �˶��¼�
	 * @param bm
	 *            �����ͼƬ
	 */
//	private void whelkRemove(View v, MotionEvent event, Bitmap bm) {
//		int x = getBitmapX(v, (int) event.getX());
//		int y = getBitmapY(v, (int) event.getY());
//		int w = bm.getWidth();
//		int h = bm.getHeight();
//		int tw = mRADIUS;
//		int th = mRADIUS;
//		int x0 = x - tw / 2;
//		if (x0 < 0)
//			x0 = 0;
//		int y0 = y - th / 2;
//		if (y0 < 0)
//			y = 0;
//		int x00 = x + tw / 2;
//		if (x00 > w)
//			x00 = w - 1;
//		int y00 = y + th / 2;
//		if (y00 > h)
//			y00 = h - 1;
//
//		int[] xx = new int[9];
//		int[] yy = new int[9];
//		float[] var = new float[9];
//		int[] mean = new int[9];
//		int[][] hist = new int[9][16];
//		int[] histAll = new int[16];
//		int histAllMax = 0;
//		int[] histMax = new int[9];
//		int indexMax=0;
//		int num=0;
//		int meanAll =0;
//		int[] origPixels = new int[w * h];
//		bm.getPixels(origPixels, 0, w, 0, 0, w, h);
//		
//		//��ͼ������copy���������о�
//		int[] origPixelsTemp = new int[w * h];
//		for(int m=0; m< h  ;m++){
//			for(int n = 0; n<w; n++){
//			origPixelsTemp[m*w+n]=origPixels[m*w + n] & 0xff;
//			//�洢��ǰֵ+�ո�
//			}
//			//��ӻس�
//		}
//		CopyOfLog copy = new CopyOfLog();
//		copy.writeArrayAsMatrix(origPixelsTemp, w);
//
//		for(int m=0;m<9;m++){
//			for(int n=0;n<16;n++){
//				hist[m][n]=0;
//			}
//		}
//		for (int m = 0; m < 3; m++) {
//			for (int n = 0; n < 3; n++) {
//				if((m*3+n)%2 == 0 ){
//					xx[m * 3 + n] = (int)(x + tw *0.7071* (n - 1));
//					yy[m * 3 + n] = (int)(y + tw * 0.7071*(m - 1));
//				}
//				else{
//					xx[m * 3 + n] = x + tw * (n - 1);
//					yy[m * 3 + n] = y + tw *(m - 1);
//				}
//				
//				mean[m * 3 + n] = 0;
//				var[m * 3 + n] = 0;
//				if (xx[m * 3 + n] < 0)
//					xx[m * 3 + n] = 0;
//				if (xx[m * 3 + n] > w - 1)
//					xx[m * 3 + n] = w - 1;
//				if (yy[m * 3 + n] < 0)
//					yy[m * 3 + n] = 0;
//				if (yy[m * 3 + n] > h - 1)
//					xx[m * 3 + n] = h - 1;
//				for (int l = 0; l < tw; l++) {
//					for (int p = 0; p < th; p++) {
//						mean[m * 3 + n] += origPixels[(xx[m * 3 + n]-tw/2 + p) * w
//								+ yy[m * 3 + n]-tw/2 + l] & 0xFF;
//						num=(int)((origPixels[(xx[m * 3 + n]-tw/2 + p) * w
//										+ yy[m * 3 + n]-tw/2 + l] & 0xFF) /16);
//						hist[m * 3 + n][num]=hist[m * 3 + n][num]+1;	
//					}
//				}
//				mean[m * 3 + n] = mean[m * 3 + n] / (tw * th);
//				meanAll = mean[m * 3 + n]+meanAll;
//				for (int l = 0; l < tw; l++) {
//					for (int p = 0; p < th; p++) {
//						var[m * 3 + n] += (mean[m * 3 + n]
//								- origPixels[(xx[m * 3 + n]-tw/2 + p) * w
//										+ yy[m * 3 + n] -th/2 + l] & 0xFF)
//								* (mean[m * 3 + n]
//										- origPixels[(xx[m * 3 + n]-tw/2 + p) * w
//												+ yy[m * 3 + n] -th/2 + l] & 0xFF);
//					}
//				}
//				var[m * 3 + n] = var[m * 3 + n] / (tw * th);
//				for(int l=0;l<16;l++){
//					histAll[l]+=histAll[l]+hist[m*3+n][l];
//				}
//				
//				}
//			}
//		for(int l=0;l<9;l++){
//			if(l == 0){
//				histAllMax = histAll[0];
//				indexMax = 0;
//			} 			    
//			else if(histMax[l]< histAll[l]){
//				histAllMax = histAll[l];
//			    indexMax = l;
//			}		
//		}
//		
//		
//		//
////		int temp=0,tempmin=255,tempmax=0;
////		for (int p=0;p<w*h;p++ ){
////			temp = origPixels[p] & 0xff;
////			if (temp<tempmin) 
////				tempmin = temp;
////			if(temp > tempmax) 
////				tempmax=temp;
////		}
//		meanAll = meanAll / 9;
//		int[] maxHist= new int[3];
//		int[] maxHistInd = new int[3];
//		maxHist[0]=hist[0][indexMax];
//		
//		//����indexMax������ǰ�������
//		for(int n=0;n<3;n++){
//			maxHist[n]=0;
//		   for(int m=0;m<9;m++){
//			   if(n==0){
//				   if(hist[m][indexMax]> maxHist[0] ){
//						maxHist[0] = hist[m][indexMax];
//					    maxHistInd[0]= m;
//					} 
//			   }
//			   else if (n==1){
//				   if(hist[m][indexMax]> maxHist[n] & hist[m][indexMax] <maxHist[n-1]){
//					    maxHist[n] = hist[m][indexMax];
//					    maxHistInd[n]= m;
//					} 
//			   }
//			   else{
//				   if(hist[m][indexMax]> maxHist[n] & hist[m][indexMax] <maxHist[n-1] & hist[m][indexMax] <maxHist[n-2] ){
//					   maxHist[n] = hist[m][indexMax];
//					   maxHistInd[n]= m;
//					} 
//			   }
//			
//		   }
//			
//		}	
//		int x1, y1, x11, y11;
//		float minVar = var[maxHistInd[0]];
//		x1 = xx[maxHistInd[0]];
//		y1 = yy[maxHistInd[0]];
//		for(int m=0; m<3;m++){
//			if(var[maxHistInd[m]]< minVar){
//				minVar =  var[maxHistInd[m]];
//				x1 = xx[maxHistInd[m]];
//				y1 = yy[maxHistInd[m]];
//			}
//		}
//
//		
////		//�����������ƽ��ֵ�����������
////		int dist = (mean[0]- meanAll)*(mean[0]- meanAll);
////		for(int n=0;n<3;n++){
////			dist =10000 ;
////		   for(int m=0;m<9;m++){
////			   if(n==0){
////				   if((mean[m]- meanAll)*(mean[m]- meanAll) < dist & m!=4){
////						dist= (mean[m]- meanAll)*(mean[m]- meanAll);
////					    closeMean[n]= mean[m];
////						closeMeanInd[n] = m;
////					} 
////			   }
////			   else if (n==1){
////				   if((mean[m]- meanAll)*(mean[m]- meanAll) < dist & m!=closeMeanInd[n-1] & m!=4){
////						dist= (mean[m]- meanAll)*(mean[m]- meanAll);
////					    closeMean[n]= mean[m];
////						closeMeanInd[n] = m;
////					} 
////			   }
////			   else{
////				   if((mean[m]- meanAll)*(mean[m]- meanAll) < dist & m!=closeMeanInd[n-1] & m!= closeMeanInd[n-2]& m!=4){
////						dist= (mean[m]- meanAll)*(mean[m]- meanAll);
////					    closeMean[n]= mean[m];
////						closeMeanInd[n] = m;
////					} 
////			   }			
////		   }		
////		}
////		
////		int x1, y1, x11, y11;
////		float minVar = var[closeMeanInd[0]];
////		x1 = xx[closeMeanInd[0]];
////		y1 = yy[closeMeanInd[0]];
////		for(int m=0; m<3;m++){
////			if(var[closeMeanInd[m]]< minVar){
////				minVar =  var[closeMeanInd[m]];
////				x1 = xx[closeMeanInd[m]];
////				y1 = yy[closeMeanInd[m]];
////			}
////		}
//////		for (int m = 0; m < 3; m++) {
//////			for (int n = 0; n < 3; n++) {
//////				if (var[m * 3 + n] < minVar) {
//////					minVar = var[m * 3 + n];
//////					x1 = xx[m * 3 + n];
//////					y1 = yy[m * 3 + n];
//////				}
//////			}
//////		}
//
//		tw = (x00 - x0)*2 + 1;
//		th = (y00 - y0)*2 + 1;
//		int r = Math.min((tw-1)/2, (th-1)/2);
//
//		int[] selection = new int[tw * th];
//		int centerX = r+1;
//		int centerY = r+1;
//		//int r = Math.min(centerX, tw - centerX)*2;
//		selectRound(selection, centerX, centerY, r, tw, th, r *3/4 );
//
//
//		
//		int[] pixels = new int[tw * th];
//		int[] layerPixels = new int[tw * th];
//
//		bm.getPixels(pixels, 0, tw, x-r, y-r, tw, th);
//		bm.getPixels(layerPixels, 0, tw, x1-r, y1-r, tw, th);
//		
//		
//	//	����Ƥ����Χ����������ΪƤ����ɫ
//		int colorLow = (indexMax-2) * 16;
//		int colorHigh = (indexMax+2) *16;
//		int color;
//		for(int m=0;m<th;m++){
//			for(int n=0; n<tw ; n++){	
//				color = layerPixels[m*tw+n]& 0xff;
//				if ( color >colorHigh || color < colorLow){
////					int colorTemp  = (colorHigh+ colorLow)/2;
////					byte temp = (byte) color;
////					colorTemp = temp;
////					layerPixels[m*tw+n] = colorTemp;
//				
//					for(int p =0 ; p< Math.min(tw-n, n);p++){
//						for(int q=0;q<Math.min(th- m, m ); q++){
//							int colorTemp = layerPixels[(m-q)*tw+n+p]& 0xff;
//							if( colorTemp < colorHigh & color > colorLow ){
//								layerPixels[m*tw+n]=layerPixels[(m-q)*tw+n+p];
//							//	break;
//							}
//							
//						}
//					}
//					
//				}
//			}
//			
//		}
//		
//		
//		// int l,temp,temp1;
//		// for (l=0;l<w*h;l++)
//		// {
//		// if(l<tw*th){
//		// pixels[l] = pixels[l] & 0xff;
//		// layerPixels[l]= layerPixels[l] &0xff;
//		// }
//		// origPixels[l]= origPixels[l] &0xff;
//		// }
//		//
//		CMTProcessor.mergeSelection(pixels, layerPixels, selection, tw, th);
//		bm.setPixels(pixels, 0, tw, x-r, y-r, tw, th);
//		mAssistBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//		Paint mPaint = new Paint();
//		mPaint.setColor(Color.GREEN);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeWidth(1.0f);
//
//		Canvas canvas = new Canvas(mAssistBitmap);
//		canvas.save();
//		canvas.drawBitmap(mAssistBitmap, 0, 0, null);
//		canvas.drawCircle(x, y, mRADIUS / 2 + 1, mPaint);
//		mPaint.setColor(Color.RED);
//		canvas.drawCircle(x1 , y1 , mRADIUS / 2 + 1, mPaint);
//		canvas.restore();
//
//		Bitmap bitmap = Bitmap.createBitmap(bm, x -  tw, y - tw ,
//				tw * 2, th * 2);
//		canvas = new Canvas(bitmap);
//		canvas.drawBitmap(bitmap, 0, 0, null);
//		mPaint.setColor(Color.GREEN);
//		canvas.drawCircle(  2 * tw,  2 * tw, mRADIUS / 2 + 1,
//				mPaint);
//
//		mZoomView.setImageBitmap(bitmap);
//		mAssistView.setImageBitmap(mAssistBitmap);
//		isFirst = false;
//	}

	/**
	 * ������ָ���������꣬���ڴ����㻭Ȧ��ʾ�û�
	 * @param v
	 * @param event
	 * @param bm
	 */
//	private void drawCircle(View v, MotionEvent event, Bitmap bm) {
//		int x = getBitmapX(v, (int) event.getX());
//		int y = getBitmapY(v, (int) event.getY());
//		int w = bm.getWidth();
//		int h = bm.getHeight();
//		int tw = mRADIUS;
//		int th = mRADIUS;
//		int x0 = x - tw / 2;
//		if (x0 < 0)
//			x0 = 0;
//		int y0 = y - th / 2;
//		if (y0 < 0)
//			y = 0;
//		int x00 = x + tw / 2;
//		if (x00 > w)
//			x00 = w - 1;
//		int y00 = y + th / 2;
//		if (y00 > h)
//			y00 = h - 1;
//		mAssistBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
//				Bitmap.Config.ARGB_8888);
//		Paint mPaint = new Paint();
//		mPaint.setColor(Color.GREEN);
//		mPaint.setStyle(Paint.Style.STROKE);
//		mPaint.setStrokeWidth(1.0f);
//		Canvas canvas = new Canvas(bm);
//		canvas = new Canvas(mAssistBitmap);
//		canvas.save();
//		canvas.drawBitmap(mAssistBitmap, 0, 0, null);
//		canvas.drawCircle(x, y, mRADIUS / 2 + 1, mPaint);
//		canvas.restore();
//		Matrix matrix = new Matrix();
//		matrix.reset();
//		Bitmap bitmap = Bitmap.createBitmap(bm, x0 - 2 * tw, y0 - tw * 2,
//				tw * 4, th * 4);
//		canvas = new Canvas(bitmap);
//		canvas.drawBitmap(bitmap, 0, 0, null);
//		canvas.drawCircle(x - x0 + 2 * tw, y - y0 + 2 * th, mRADIUS / 2 + 1,
//				mPaint);
//		mZoomView.setImageBitmap(bitmap);
//		mAssistView.setImageBitmap(mAssistBitmap);
//	}

	@Override
	public void release() {
		super.release();
	}
}

	


