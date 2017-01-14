package cn.jingling.lib.filters;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class ByteCurve {
	byte[] mCurveRed = null;
	byte[] mCurveGreen = null;
	byte[] mCurveBlue = null;

	public ByteCurve(Context cx, String file) {
		try {
			makeCurve(cx.getAssets().open(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public byte[] getCurveRed() {
		return mCurveRed;
	}

	public byte[] getCurveGreen() {
		return mCurveGreen;
	}

	public byte[] getCurveBlue() {
		return mCurveBlue;
	}

	private void makeCurve(InputStream iStream) {

		mCurveRed = new byte[256];
		mCurveGreen = new byte[256];
		mCurveBlue = new byte[256];
		// mCurveAll = new int[256];

		// TODO Auto-generated method stub
		int i;

		try {

			DataInputStream in = new DataInputStream(new BufferedInputStream(
					iStream));
			// for (i = 0; i < 256; i++) {
			// mCurveAll[i] = in.readByte();
			// if (mCurveAll[i] < 0)
			// mCurveAll[i] += 256;
			// }
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

	// int[] mCurveAll = null;
}