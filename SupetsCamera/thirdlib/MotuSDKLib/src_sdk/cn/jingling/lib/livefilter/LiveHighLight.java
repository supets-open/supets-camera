package cn.jingling.lib.livefilter;

import android.content.Context;
import android.opengl.GLES20;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.livefilter.ShaderHelper.ShaderInfo;

/**
 * 夜拍会使用这个滤镜
 * 
 * @Copyright(C)2013, Baidu.Tech.Co.Ltd. All rights reserved.
 * @Filename: LiveHighLight.java Created On 2014-1-14
 * @Author:zhuchen
 * @Description:TODO
 * 
 * @Version:1.0
 * @Update:
 */
public class LiveHighLight extends LiveOp {

	private ShaderInfo mShader;

	@Override
	public void glSetup(Context cx) {
		// TODO Auto-generated method stub
		/*
		 * mShader = ShaderHelper.glGenerateShader(cx, R.raw.test_vertex_shader,
		 * R.raw.test_fragment_shader, "aPosition", "uMVPMatrix", "uTexture");
		 */
		mShader = ShaderHelper.glGenerateShader(cx, "vertex_shader",
				"high_light_fragment_shader", "aPosition", "uMVPMatrix",
				"uTexture");

	}

	@Override
	public void glDraw(float[] mvpMatrix, int vboHandle, FrameBufferInfo fbi,
			int posDataSize, int textureHandle) {
		// TODO Auto-generated method stub
		GLES20.glUseProgram(mShader.program);
		GLES20.glViewport(0, 0, mImageSize.x, mImageSize.y);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbi == null ? 0
				: fbi.frameBufferHandle);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
		GLES20.glUniform1i(mShader.uniforms.get("uTexture"), 0);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboHandle);
		GLES20.glVertexAttribPointer(mShader.attribute, posDataSize,
				GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(mShader.attribute);
		GLES20.glUniformMatrix4fv(mShader.uniforms.get("uMVPMatrix"), 1, false,
				mvpMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);
	}

	@Override
	public void glRelease() {
		// TODO Auto-generated method stub

	}

}
