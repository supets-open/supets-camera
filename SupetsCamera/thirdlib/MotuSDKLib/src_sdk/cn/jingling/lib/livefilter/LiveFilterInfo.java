package cn.jingling.lib.livefilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Point;
import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.LiveLayer.Type;

public class LiveFilterInfo {

	private String mLabel;
	private ArrayList<LiveOp> mOps;
	private boolean mNeedSetup;

	private LiveFilterInfo(String label, LiveOp... ops) {
		this.mLabel = label;
		mOps = new ArrayList<LiveOp>();
		for (int i = 0; i < ops.length; i++) {
			mOps.add(ops[i]);
		}
		mNeedSetup = true;
	}

	/**
	 * 
	 * @return the label of this filter
	 */
	public String getLabel() {
		return mLabel;
	}

	/**
	 * 
	 * @return TRUE if this filter will smooth skin, FALSE otherwise.
	 */
	public boolean smooth() {
		for (LiveOp op : mOps) {
			if (op instanceof LiveSmooth) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Invoke when startup
	 */
	public void setup() {
		mNeedSetup = true;
	}

	/**
	 * Invoke when change filter
	 * 
	 * @param cx
	 * @param fboImageSize
	 *            Image size of the preview data, Point.x is the width, Point.y
	 *            is the height.
	 */
	public void glUpdate(Context cx, Point fboImageSize) {
		if (mNeedSetup) {
			for (LiveOp op : mOps) {
				op.glSetup(cx);
				mNeedSetup = false;
			}
		}

		for (LiveOp op : mOps) {
			op.glUpdate(cx, fboImageSize);
		}
	}

	public void prepareBmForTexture(Context cx, Point fboImageSize) {
		for (LiveOp op : mOps) {
			op.prepareBmForTexture(cx, fboImageSize);
		}
	}

	/**
	 * Invoke in onDrawFrame to render the data every frame.
	 * 
	 * @param mvpMatrix
	 *            matrix for rendering
	 * @param vboHandle
	 *            Vertex Buffer Object for rendering by glDrawArrays
	 * @param textureHandle
	 *            Texture to draw
	 * @param frameBufferSwap
	 *            Need 2 frame buffer objects for the temporary data storage.
	 * @return Output frame buffer object contains the rendered result.
	 */
	public FrameBufferInfo glDraw(float[] mvpMatrix, int vboHandle,
			int textureHandle, FrameBufferInfo[] frameBufferSwap) {
		int now = 0;
		int size = mOps.size();
		frameBufferSwap[now].textureHandle = textureHandle;
		for (int i = 0; i < size; i++) {
			mOps.get(i).glDraw(mvpMatrix, vboHandle,
					frameBufferSwap[(now + 1) % 2],
					CameraRenderInteface.POS_DATA_SIZE,
					frameBufferSwap[now].textureHandle);
			now = (now + 1) % 2;
		}
		GLHelper.glCheckError();
		return frameBufferSwap[now];
	}

	/**
	 * Release relevant resources
	 */
	public void glRelease() {
		for (LiveOp op : mOps) {
			op.glRelease();
		}
	}

	/**
	 * Generate all live filters
	 * 
	 * @param smooth
	 *            smooth skin or not
	 * @return All live filters map: label to LiveFilterInfo
	 */
	public static Map<String, LiveFilterInfo> generateLiveFilters(Context cx, boolean smooth) {
		PackageSecurity.check(cx);
		Map<String, LiveFilterInfo> ret = new HashMap<String, LiveFilterInfo>();
		// ret.put("test", new LiveFilterInfo("test", new LiveCurve(
		// "curves/louguang2.dat"), new LiveLayer("layers/louguang2",
		// Type.DARKEN)));
		// 人像
		if (smooth) {
			ret.put("skinbeeps", new LiveFilterInfo("skinbeeps",new LiveSkinDetect()));
			ret.put("clfugu", new LiveFilterInfo("clfugu", new LiveSmooth(),
					new LiveCurve("curves/live_fugu.dat"), new LiveLayer(
							"layers/live_fugu", Type.MULTIPLY)));
			ret.put("ctianmei", new LiveFilterInfo("ctianmei",
					new LiveSmooth(), new LiveSaturation(0.8f), new LiveCurve(
							"curves/camera_tianmei.dat"), new LiveLayer(
							"layers/camera_tianmei", Type.MULTIPLY, 0.1f)));
			ret.put("clvivid", new LiveFilterInfo("clvivid", new LiveSmooth(),
					new LiveSaturation(1.2f), new LiveCurve(
							"curves/live_vivid.dat")));
			ret.put("cllomo", new LiveFilterInfo("cllomo", new LiveSmooth(),
					new LiveSaturation(0.9f), new LiveCurve(
							"curves/live_lomo.dat"), new LiveLayer(
							"layers/live_lomo", Type.LINEAR_BURN, 0.4f)));
			ret.put("clrixi", new LiveFilterInfo("clrixi", new LiveSmooth(),
					new LiveSaturation(0.8f), new LiveCurve(
							"curves/live_rixi.dat")));
			ret.put("clweimei", new LiveFilterInfo("clweimei",
					new LiveSmooth(), new LiveSaturation(0.8f), new LiveCurve(
							"curves/live_weimei.dat")));
			ret.put("justsmooth", new LiveFilterInfo("justsmooth",
					new LiveSmooth()));
			ret.put("cmeibai", new LiveFilterInfo("cmeibai", new LiveSmooth(),
					new LiveSaturation(0.85f), new LiveCurve(
							"curves/camera_meibai.dat")));
			ret.put("cqingxin", new LiveFilterInfo("cqingxin",
					new LiveSmooth(),
					new LiveCurve("curves/camera_qingxin.dat")));

		} else {
			ret.put("clfugu", new LiveFilterInfo("clfugu", new LiveCurve(
					"curves/live_fugu.dat"), new LiveLayer("layers/live_fugu",
					Type.MULTIPLY)));
			ret.put("ctianmei", new LiveFilterInfo("ctianmei",
					new LiveSaturation(0.8f), new LiveCurve(
							"curves/camera_tianmei.dat"), new LiveLayer(
							"layers/camera_tianmei", Type.MULTIPLY, 0.1f)));
			ret.put("clvivid", new LiveFilterInfo("clvivid",
					new LiveSaturation(1.2f), new LiveCurve(
							"curves/live_vivid.dat")));
			ret.put("cllomo", new LiveFilterInfo("cllomo", new LiveSaturation(
					0.9f), new LiveCurve("curves/live_lomo.dat"),
					new LiveLayer("layers/live_lomo", Type.LINEAR_BURN, 0.4f)));
			ret.put("clrixi", new LiveFilterInfo("clrixi", new LiveSaturation(
					0.8f), new LiveCurve("curves/live_rixi.dat")));
			ret.put("clweimei", new LiveFilterInfo("clweimei",
					new LiveSaturation(0.8f), new LiveCurve(
							"curves/live_weimei.dat")));
			ret.put("justsmooth", new LiveFilterInfo("justsmooth"));
			ret.put("cmeibai", new LiveFilterInfo("cmeibai",
					new LiveSaturation(0.85f), new LiveCurve(
							"curves/camera_meibai.dat")));
			ret.put("cqingxin", new LiveFilterInfo("cqingxin", new LiveCurve(
					"curves/camera_qingxin.dat")));

		}
		// 风景
		ret.put("clrise", new LiveFilterInfo("clrise", new LiveLayer(
				"layers/live_rise", Type.OVERLAY, 0.4f), new LiveCurve(
				"curves/live_rise.dat")));
		ret.put("clwalden", new LiveFilterInfo("clwalden", new LiveCurve(
				"curves/live_walden.dat"), new LiveLayer("layers/live_walden",
				Type.OVERLAY, 0.4f)));
		ret.put("clhefe", new LiveFilterInfo("clhefe", new LiveLayer(
				"layers/live_hefe_m", Type.MULTIPLY, 0.8f), new LiveLayer(
				"layers/live_hefe_o", Type.OVERLAY, 0.2f), new LiveCurve(
				"curves/live_hefe.dat")));
		ret.put("clfuguscenery", new LiveFilterInfo("clfuguscenery",
				new LiveCurve("curves/live_fugu_scenery.dat")));
		ret.put("cllomoscenery", new LiveFilterInfo("cllomoscenery",
				new LiveLayer("layers/live_lomo_scenery", Type.OVERLAY, 0.4f),
				new LiveCurve("curves/live_lomo_scenery.dat")));
		ret.put("cqiuse", new LiveFilterInfo("cqiuse", new LiveCurve(
				"curves/camera_qiuse.dat")));
		ret.put("clenhance", new LiveFilterInfo("clenhance",
				new LiveSceneEnhance("curves/color_enhance.dat")));
		// 智能
		ret.put("cljiuguan", new LiveFilterInfo("cljiuguan", new LiveLayer(
				"layers/live_jiuguan", Type.OVERLAY, 0.65f), new LiveCurve(
				"curves/live_jiuguan.dat")));
		ret.put("cldushi", new LiveFilterInfo("cldushi", new LiveCurve(
				"curves/live_dushi1.dat"), new LiveLayer("layers/live_dushi",
				Type.OVERLAY), new LiveCurve("curves/live_dushi2.dat")));
		ret.put("clguangyin", new LiveFilterInfo("clguangyin", new LiveCurve(
				"curves/live_guangyin1.dat"), new LiveLayer(
				"layers/live_guangyin", Type.OVERLAY, 0.3f),
				new LiveSaturation(0.45f), new LiveCurve(
						"curves/live_guangyin2.dat")));
		ret.put("clyazhi", new LiveFilterInfo("clyazhi", new LiveLayer(
				"layers/live_yazhi", Type.OVERLAY, 0.3f), new LiveCurve(
				"curves/live_yazhi.dat")));
		ret.put("clzaoan", new LiveFilterInfo("clzaoan", new LiveLayer(
				"layers/live_zaoan", Type.OVERLAY, 0.85f), new LiveSaturation(
				0.75f), new LiveCurve("curves/live_zaoan.dat")));
		ret.put("clvividscenery", new LiveFilterInfo("clvividscenery",
				new LiveSaturation(1.2f),
				new LiveCurve("curves/live_vivid.dat")));

		// 美食
		ret.put("clfood1", new LiveFilterInfo("clfood1", new LiveSaturation(
				1.15f), new LiveCurve("curves/live_food1.dat"), new LiveLayer(
				"layers/live_food1", Type.MULTIPLY, 0.1f)));
		ret.put("clfood2", new LiveFilterInfo("clfood2", new LiveLayer(
				"layers/live_food2", Type.OVERLAY, 0.55f), new LiveCurve(
				"curves/live_food2.dat")));
		ret.put("clfood3", new LiveFilterInfo("clfood3", new LiveCurve(
				"curves/live_food3.dat"), new LiveLayer("layers/live_food3",
				Type.OVERLAY, 0.5f)));
		ret.put("clfood4", new LiveFilterInfo("clfood4", new LiveSaturation(
				0.7f), new LiveLayer("layers/live_food4", Type.MULTIPLY, 0.6f)));
		ret.put("clfood5", new LiveFilterInfo("clfood5", new LiveLayer(
				"layers/live_food5", Type.OVERLAY, 0.6f)));
		ret.put("clfood6", new LiveFilterInfo("clfood6", new LiveCurve(
				"curves/live_food6.dat")));

		// 新增
		ret.put("clheibai", new LiveFilterInfo("clheibai", new LiveSaturation(
				0.0f), new LiveCurve("curves/live_heibai.dat")));
		ret.put("clcaisefupian", new LiveFilterInfo("clcaisefupian",
				new LiveSaturation(0.85f), new LiveCurve(
						"curves/live_caisefupian.dat")));
		ret.put("clmidway", new LiveFilterInfo("clmidway", new LiveLayer(
				"layers/live_midway", Type.OVERLAY, 0.6f), new LiveSaturation(
				0.8f), new LiveCurve("curves/live_midway.dat")));
		ret.put("clm3", new LiveFilterInfo("clm3", new LiveLayer(
				"layers/live-m3", Type.OVERLAY, 0.3f),
				new LiveSaturation(0.75f), new LiveCurve("curves/live-m3.dat")));
		ret.put("clgoldfinch", new LiveFilterInfo("clgoldfinch",
				new LiveSaturation(0.7f), new LiveCurve(
						"curves/live-goldfinch-1.dat"), new LiveCurve(
						"curves/live-goldfinch-2.dat")));
		ret.put("clmeadow", new LiveFilterInfo("clmeadow", new LiveSaturation(
				0.6f), new LiveCurve("curves/live-meadow-1.dat"),
				new LiveCurve("curves/live-meadow-2.dat")));
		ret.put("cllouguang", new LiveFilterInfo("cllouguang", new LiveLayer(
				"layers/live_louguang_1", Type.OVERLAY, 0.5f), new LiveLayer(
				"layers/live_louguang_2", Type.SCREEN, 1.0f)));

		ret.put("clyanli", new LiveFilterInfo("clyanli", new LiveSaturation(
				1.3f), new LiveCurve("curves/live_yanli.dat"), new LiveLayer(
				"layers/live_yanli", Type.OVERLAY, 0.4f)));

		// New Year 贴纸
		ret.put("newyear1", new LiveFilterInfo("newyear1", new LiveLayer(
				"layers/newyear1", Type.COVERAGE, 1.0f)));
		ret.put("newyear2", new LiveFilterInfo("newyear2", new LiveLayer(
				"layers/newyear2", Type.COVERAGE, 1.0f)));
		ret.put("newyear3", new LiveFilterInfo("newyear3", new LiveLayer(
				"layers/newyear3", Type.COVERAGE, 1.0f)));
		ret.put("newyear4", new LiveFilterInfo("newyear4", new LiveLayer(
				"layers/newyear4", Type.COVERAGE, 1.0f)));
		ret.put("newyear5", new LiveFilterInfo("newyear5", new LiveLayer(
				"layers/newyear5", Type.COVERAGE, 1.0f)));

		// 艺术滤镜
		ret.put("watercolor", new LiveFilterInfo("watercolor",
				new LiveWaterColor()));
		ret.put("pencil", new LiveFilterInfo("pencil", new LivePencil()));
		ret.put("colorpencil", new LiveFilterInfo("colorpencil",
				new LiveColorPencil()));
		ret.put("bluecolor",
				new LiveFilterInfo("bluecolor", new LiveBlueEdge()));
		ret.put("hopeeffect", new LiveFilterInfo("hopeeffect",
				new LiveHopeEffect()));
		ret.put("oilpainting", new LiveFilterInfo("oilpainting"));
		ret.put("kirsch", new LiveFilterInfo("kirsch", new LiveKirsch()));
		ret.put("halftone", new LiveFilterInfo("halftone"));
		ret.put("halftonegray", new LiveFilterInfo("halftonegray"));
		ret.put("clahergb3", new LiveFilterInfo("clahergb3"));

		ret.put("original", new LiveFilterInfo("original"));

		// 夜拍
		ret.put("clshadowhighlight", new LiveFilterInfo("clshadowhighlight",
				new LiveHighLight()));
		return ret;
	}
}
