package cn.jingling.lib.livefilter;

import java.util.HashMap;

import cn.jingling.lib.utils.LogUtils;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

public class ShaderHelper {
	private static final String TAG = "ShaderHelper";

	/**
	 * Helper function to compile a shader.
	 * 
	 * @param shaderType
	 *            The shader type.
	 * @param shaderSourceId
	 *            The shader source code raw id.
	 * @return An OpenGL handle to the shader.
	 */
	public static int compileShader(Context cx, final int shaderType,
			final int shaderSourceId) {
		String shaderSource = RawResourceReader.readTextFileFromRawResource(cx,
				shaderSourceId);
		return compileShader(shaderType, shaderSource);
	}

	/**
	 * Helper function to compile a shader.
	 * 
	 * @param shaderType
	 *            The shader type.
	 * @param shaderSource
	 *            The shader source code.
	 * @return An OpenGL handle to the shader.
	 */
	public static int compileShader(final int shaderType,
			final String shaderSource) {
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) {
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS,
					compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {
				Log.e(TAG,
						"Error compiling shader: "
								+ GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0) {
			Log.e(TAG, shaderSource);
			throw new RuntimeException("Error creating shader.");
		}

		return shaderHandle;
	}

	/**
	 * Helper function to compile and link a program.
	 * 
	 * @param vertexShaderHandle
	 *            An OpenGL handle to an already-compiled vertex shader.
	 * @param fragmentShaderHandle
	 *            An OpenGL handle to an already-compiled fragment shader.
	 * @param attributes
	 *            Attributes that need to be bound to the program.
	 * @return An OpenGL handle to the program.
	 */
	public static int createAndLinkProgram(final int vertexShaderHandle,
			final int fragmentShaderHandle, final String[] attributes) {
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) {
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);

			// Bind attributes
			if (attributes != null) {
				final int size = attributes.length;
				for (int i = 0; i < size; i++) {
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}
			}

			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS,
					linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) {
				Log.e(TAG,
						"Error compiling program: "
								+ GLES20.glGetProgramInfoLog(programHandle));
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0) {
			throw new RuntimeException("Error creating program.");
		}

		return programHandle;
	}

	public static ShaderInfo glGenerateShader(Context cx, int vertexId,
			int fragmentId, String attrib, String... uniforms) {
		String vertexSource = RawResourceReader.readTextFileFromRawResource(cx,
				vertexId);
		String fragmentSource = RawResourceReader.readTextFileFromRawResource(
				cx, fragmentId);
		return glGenerateShader(vertexSource, fragmentSource, attrib, uniforms);
	}

	public static ShaderInfo glGenerateShader(Context cx, String vertexId,
			String fragmentId, String attrib, String... uniforms) {
		int programHandle = Opengl20JniLib.getShaderProgram(vertexId, fragmentId, attrib);
		//LogUtils.e("xxxx", "programHandle = " + programHandle);
		ShaderInfo info = new ShaderInfo(programHandle);
		info.attribute = GLES20.glGetAttribLocation(programHandle, attrib);
		for (int i = 0; i < uniforms.length; i++) {
			info.uniforms.put(uniforms[i],
					GLES20.glGetUniformLocation(programHandle, uniforms[i]));
		}
		//LogUtils.e("xxxx", "ShaderInfo = " + info.attribute + ", " + info.uniforms.toString());
		return info;
	}
	
	/*public static ShaderInfo glGenerateShader(Context cx, String vertexId,
			String fragmentId, String attrib, String... uniforms) {
		return glGenerateShader(Opengl20JniLib.getShader(vertexId),
				Opengl20JniLib.getShader(fragmentId), attrib, uniforms);
	}*/

	public static class ShaderInfo {
		public int program;
		public int attribute;
		public HashMap<String, Integer> uniforms = new HashMap<String, Integer>();

		public ShaderInfo(int program) {
			this.program = program;
		}
	}

	private static ShaderInfo glGenerateShader(String vertexSource,
			String fragmentSource, String attrib, String... uniforms) {
		int vShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER,
				vertexSource);

		int fShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER,
				fragmentSource);
		int programHandle = ShaderHelper.createAndLinkProgram(vShader, fShader,
				new String[] { attrib });
		ShaderInfo info = new ShaderInfo(programHandle);
		info.attribute = GLES20.glGetAttribLocation(programHandle, attrib);
		for (int i = 0; i < uniforms.length; i++) {
			info.uniforms.put(uniforms[i],
					GLES20.glGetUniformLocation(programHandle, uniforms[i]));
		}
		return info;
	}

}
