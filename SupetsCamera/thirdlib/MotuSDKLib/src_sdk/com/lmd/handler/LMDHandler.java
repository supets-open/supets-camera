package com.lmd.handler;

public class LMDHandler {
	static {
		System.loadLibrary("VISFaceProcHandler");
	}

	/**
	 * Description : 初始化函数 　　
	 * 
	 * @param： mdlPath 模型路径 　　
	 * @return boolean： 判断初始化是否成功 　
	 */
	public native boolean LMDInitHandler(String mdlPath);

	/**
	 * Description : 释放相关资源 　
	 */
	public native void LMDDestoryHandler();

	/**
	 * Description : 工作函数 　　
	 * 
	 * @param： imgData 输入图像数据 （bgr三通道 倒立）
	 * 
	 * 
	 * @param： imgWidth 输入图像宽度
	 * @param： imgHeight 输入图像高度
	 * @param： fScale 缩放因子 推荐缩放因子 int minsize = width < height ? width : height;
	 *         float fScale = (1.3f); if (minsize >= 1920) fScale = (6.0f); else
	 *         if (minsize >= 1024) fScale = (4.5f); else if (minsize >= 640)
	 *         fScale = (3.0f); else if (minsize >= 320) fScale = (2.0f);
	 * 
	 * @return int[]： 检测结果 结果存放格式，总共87个点[x1, y1, x2, y2 .... x87,y87] 形状点描述：
	 *         形状点由87个点组成： 1-8 左眼，以内眼角为第一个点逆时针旋转 9-16 右眼 以内眼角为第一个点顺时针旋转 17-26
	 *         左眉，以眉毛内侧为第一个点逆时针旋转 27-36 右眉 以眉毛内侧为第一个点顺时针旋转 37-48 鼻子 以鼻子左侧开始为第一个点
	 *         49-60 嘴唇外轮廓 以左嘴角为第一个点顺时针旋转 61-68 嘴唇内轮廓 以左内嘴角为第一个点顺时针旋转 69-87 脸部轮廓
	 *         以脸部左侧开始为第一个点
	 */
	public native int[] LMDWorker(byte[] imgData, int imgWidth,
			int imgHeight, float fScale);


}
