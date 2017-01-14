package cn.jingling.lib.livefilter;

import java.nio.IntBuffer;

import android.content.Context;
import android.opengl.GLES20;
import android.text.TextUtils;
import cn.jingling.lib.livefilter.BufferHelper.FrameBufferInfo;
import cn.jingling.lib.utils.ErrorHandleHelper;

public class GLHelper {
	
	private static final String TAG = "GLHelper";
	
	public static void glCheckError(String msg) {
		int error = GLES20.glGetError();
		if (error != GLES20.GL_NO_ERROR) {
			String s = "OpenGL Error: " + error;
			if (!TextUtils.isEmpty(msg)) {
				s += " (" + msg + ")";
			}
			ErrorHandleHelper.handleErrorMsg(s, TAG);
		}
	}

	public static void glCheckError() {
		glCheckError(null);
	}
	
	
	
	public static int glGetMaxTextureSize() {
		IntBuffer params = IntBuffer.allocate(1);

		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, params);
		params.position(0);
		int maxTextureSize = params.get(0);
		return maxTextureSize;
	}
	
}
