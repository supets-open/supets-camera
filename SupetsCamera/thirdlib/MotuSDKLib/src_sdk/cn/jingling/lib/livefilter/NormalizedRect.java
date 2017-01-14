package cn.jingling.lib.livefilter;

/** 逐块处理时使用。存储图像块相对于原图的位置参数。坐标采用Android Graphics坐标:left<=right, top<=bottom.
 *
 */
public class NormalizedRect {
	public float left = 0.0f;
	public float top = 0.0f;
	public float right = 1.0f;
	public float bottom = 1.0f;
	
	
	
	/** left<=right, top<=bottom
	 * @param left [0.0, 1.0]
	 * @param right [0.0, 1.0]
	 * @param top [0.0, 1.0]
	 * @param bottom [0.0, 1.0]
	 */
	public NormalizedRect(float left, float right, float top, float bottom) {
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

}
