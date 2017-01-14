package cn.jingling.lib.filters;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Curve {
	int[] mCurveRed = null;
	int[] mCurveGreen = null;
	int[] mCurveBlue = null;
//	int[] mCurveAll = null;

	public Curve() {
		makeCurve();
	}

	public Curve(Context cx, String file) {
		this(cx, false, file);
	}

	public Curve(Context cx, boolean sdcard, String file) {
		try {
			if (sdcard) {
				File f = new File(file);
				long len = f.length();
				if (len > 800) {
					makeCompleteCurve(new FileInputStream(file));
				} else {
					makeCurve(new FileInputStream(file));
				}
			} else {
				long len = cx.getAssets().open(file).available();
				if (len > 800) {
					makeCompleteCurve(cx.getAssets().open(file));
				} else {
					makeCurve(cx.getAssets().open(file));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot find this curve: " + file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int[] getCurveRed() {
		return mCurveRed;
	}

	public int[] getCurveGreen() {
		return mCurveGreen;
	}

	public int[] getCurveBlue() {
		return mCurveBlue;
	}

	private void makeCurve() {
//		mCurveAll = new int[256];
		mCurveRed = new int[256];
		mCurveGreen = new int[256];
		mCurveBlue = new int[256];

		int i;
		for (i = 0; i < 128; i++) {
			mCurveRed[i] = (int) (0.7559 * i);
			mCurveGreen[i] = (int) (1.252 * i);
			mCurveBlue[i] = (int) (1.252 * i);
		}
		for (i = 128; i < 256; i++) {
			mCurveRed[i] = (int) (1.2422 * i - 62);
			mCurveGreen[i] = (int) (0.75 * i + 63);
			mCurveBlue[i] = (int) (0.75 * i + 63);
		}
	}
	
	private void makeCompleteCurve(InputStream iStream) {
		int[] curveAll = new int[256];
		mCurveRed = new int[256];
		mCurveGreen = new int[256];
		mCurveBlue = new int[256];

		// TODO Auto-generated method stub
		int i;

		try {

			DataInputStream in = new DataInputStream(new BufferedInputStream(
					iStream));
			for (i = 0; i < 256; i++) {
				curveAll[i] = in.readByte();
				if (curveAll[i] < 0)
					curveAll[i] += 256;
			}
			for (i = 0; i < 256; i++) {
				mCurveRed[i] = in.readByte();
				if (mCurveRed[i] < 0)
					mCurveRed[i] += 256;
				mCurveRed[i] = curveAll[mCurveRed[i]];
			}
			for (i = 0; i < 256; i++) {
				mCurveGreen[i] = in.readByte();
				if (mCurveGreen[i] < 0)
					mCurveGreen[i] += 256;
				mCurveGreen[i] = curveAll[mCurveGreen[i]];
			}
			for (i = 0; i < 256; i++) {
				mCurveBlue[i] = in.readByte();
				if (mCurveBlue[i] < 0)
					mCurveBlue[i] += 256;
				mCurveBlue[i] = curveAll[mCurveBlue[i]];
			}

			in.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void makeCurve(InputStream iStream) {

		mCurveRed = new int[256];
		mCurveGreen = new int[256];
		mCurveBlue = new int[256];
		// mCurveAll = new int[256];

		// TODO Auto-generated method stub
		int i;

		try {

			DataInputStream in = new DataInputStream(new BufferedInputStream(
					iStream));
			for (i = 0; i < 256; i++) {
				mCurveRed[i] = in.readByte();
				if (mCurveRed[i] < 0)
					mCurveRed[i] += 256;
			}
			for (i = 0; i < 256; i++) {
				mCurveGreen[i] = in.readByte();
				if (mCurveGreen[i] < 0)
					mCurveGreen[i] += 256;
			}
			for (i = 0; i < 256; i++) {
				mCurveBlue[i] = in.readByte();
				if (mCurveBlue[i] < 0)
					mCurveBlue[i] += 256;
			}

			in.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}