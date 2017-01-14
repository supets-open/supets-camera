#include "CMTProcessor.h"

#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, "Native", __VA_ARGS__)

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_shadowAndHighlight
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint lowV, jint highV)
{
	jint *pixels =  (*env)->GetIntArrayElements(env, srcPixArray, 0);
	shadowAndHighlight(pixels, w, h, lowV, highV);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_skinOverLay
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray dstPixArray, jint w, jint h)
{
	jint* srcpixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* dstpixels = (*env)->GetIntArrayElements(env, dstPixArray, 0);
	skinOverLay(srcpixels,dstpixels,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, srcpixels, 0);
	(*env)->ReleaseIntArrayElements(env, dstPixArray, dstpixels, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_beepsDetailRecover
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray dstPixArray)
{
	jint* srcPix = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* dstPix = (*env)->GetIntArrayElements(env, dstPixArray, 0);
	beepsDetailRecover(srcPix, dstPix);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, srcPix, 0);
	(*env)->ReleaseIntArrayElements(env, dstPixArray, dstPix, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_beepsOverlay
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray dstPixArray)
{
	jint* srcPix = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* dstPix = (*env)->GetIntArrayElements(env, dstPixArray, 0);
	beepsOverlay(srcPix, dstPix);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, srcPix, 0);
	(*env)->ReleaseIntArrayElements(env, dstPixArray, dstPix, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_beepsSetupAll
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	beepsSetupAll(pixels, w, h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_BEEPSVerticalHorizontal
 (JNIEnv *env, jclass obj, jdoubleArray data, jint w, jint h)
{
	jdouble* pixels = (*env)->GetDoubleArrayElements(env, data, 0);
	BEEPSVerticalHorizontal(pixels, w, h);
	(*env)->ReleaseDoubleArrayElements(env, data, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_BEEPSHorizontalVertical
 (JNIEnv *env, jclass obj, jdoubleArray data, jint w, jint h)
{
	jdouble* pixels = (*env)->GetDoubleArrayElements(env, data, 0);
	BEEPSHorizontalVertical(pixels, w, h);
	(*env)->ReleaseDoubleArrayElements(env, data, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_ViberationInitial
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	ViberationInitial(pixels,w,h);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_ViberationControl
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jfloat degree)
{
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	ViberationControl(pixels,w,h, degree);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_ViberationRelease
 (JNIEnv *env, jclass obj)
{
	ViberationRelease();
}


JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_gifProcess
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jintArray p, jint mouthCondition, jint browCondition, jint eyeCondition)
{
	jint *pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint *point = (*env)->GetIntArrayElements(env, p, 0);
	gifProcess(pixels, w, h, point, mouthCondition, browCondition, eyeCondition);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, p, point, 0);
}

JNIEXPORT jobject JNICALL Java_cn_jingling_lib_filters_CMTProcessor_computeHueExpectationAndVariance(
		JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h) {
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jclass clazz = (*env)->FindClass(env, "cn/jingling/lib/filters/ExpVar");
	jmethodID jmid = (*env)->GetMethodID(env, clazz, "<init>", "()V");
	jobject jexpvar = (*env)->NewObject(env, clazz, jmid);
	jfieldID jexpect = (*env)->GetFieldID(env, clazz, "expect", "I");
	jfieldID jvar = (*env)->GetFieldID(env, clazz, "var", "I");
	ExpVar ev = computeHueExpectationAndVariance(pixels, w, h);
	(*env)->SetIntField(env, jexpvar, jexpect, ev.expect);
	(*env)->SetIntField(env, jexpvar, jvar, ev.var);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	return jexpvar;
}


JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_MSRCR
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
 {
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	MSRCR(pixels,w,h);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

 }

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_setupDecolorization
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	setupDecolorization(pixels,w,h);

	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_decolorization
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint size)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	Decolorization(pixels,size);

	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_whiteBalance
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
 {
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	whiteBalance(pixels,w,h);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

 }
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_HDRsimple
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint block_num, jint edg_thre)
 {
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	HDRsimple(pixels,w,h,block_num, edg_thre);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

 }
//////////////////////////////////////////////////cheng paper_artist /////////////////////////////////////////////////////////////
//public native static int lipstick(int [] Pixels, int [] color, int w, int h, int [] p);//cheng
JNIEXPORT int JNICALL Java_cn_jingling_lib_filters_CMTProcessor_lipstick
 (JNIEnv *env, jclass obj, jintArray Pixels, jintArray color, jint w, jint h, jintArray p)
{
 	jint* pixels  = (*env)->GetIntArrayElements(env, Pixels, 0);
 	jint* colorpixels = (*env)->GetIntArrayElements(env, color, 0);
 	jint* points  = (*env)->GetIntArrayElements(env, p, 0);
 	lipstick(pixels ,colorpixels, w, h, points);
 	(*env)->ReleaseIntArrayElements(env, Pixels, pixels , 0);
 	(*env)->ReleaseIntArrayElements(env, color, colorpixels, 0);
 	(*env)->ReleaseIntArrayElements(env, p, points, 0);
}



JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_Kirsch
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint kind)
{
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	Kirsch(pixels,w,h,kind);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_Posterize
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint levels)
{
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	Posterize(pixels,w,h,levels);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_WaterColor
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray srcPixArray1, jint w, jint h)
{
 	jint* pixels  = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	jint* pixels1 = (*env)->GetIntArrayElements(env, srcPixArray1, 0);
 	WaterColor(pixels ,pixels1, w, h);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels , 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray1, pixels1, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_Pencil
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray srcPixArray1,jintArray srcPixArray2, jint w, jint h)
{
 	jint* pixels  = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	jint* pixels1 = (*env)->GetIntArrayElements(env, srcPixArray1, 0);
 	jint* pixels2 = (*env)->GetIntArrayElements(env, srcPixArray2, 0);
 	Pencil(pixels ,pixels1,pixels2, w, h);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels , 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray1, pixels1, 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray2, pixels2, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_ColorPencil
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray srcPixArray1,jintArray srcPixArray2, jint w, jint h)
{
 	jint* pixels  = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	jint* pixels1 = (*env)->GetIntArrayElements(env, srcPixArray1, 0);
 	jint* pixels2 = (*env)->GetIntArrayElements(env, srcPixArray2, 0);
 	ColorPencil(pixels ,pixels1,pixels2, w, h);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels , 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray1, pixels1, 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray2, pixels2, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_BlueEdg
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray srcPixArray1,jintArray srcPixArray2, jint w, jint h)
{
 	jint* pixels  = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	jint* pixels1 = (*env)->GetIntArrayElements(env, srcPixArray1, 0);
 	jint* pixels2 = (*env)->GetIntArrayElements(env, srcPixArray2, 0);
 	BlueEdg(pixels ,pixels1,pixels2, w, h);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels , 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray1, pixels1, 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray2, pixels2, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_OilPainting
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray srcPixArray1,jintArray srcPixArray2, jint w, jint h)
{
 	jint* pixels  = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	jint* pixels1 = (*env)->GetIntArrayElements(env, srcPixArray1, 0);
 	jint* pixels2 = (*env)->GetIntArrayElements(env, srcPixArray2, 0);
 	OilPainting(pixels ,pixels1,pixels2, w, h);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels , 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray1, pixels1, 0);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray2, pixels2, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_setHopeEffectTexturePixels
 (JNIEnv *env, jclass obj, jintArray texArray, jint w, jint h)
{
 	jint* pixels  = (*env)->GetIntArrayElements(env, texArray, 0);
 	setHopeEffectTexturePixels(pixels, w, h);
 	(*env)->ReleaseIntArrayElements(env, texArray, pixels , 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_HopeEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint *pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	HopeEffect(pixels, w, h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_HalfTone
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jintArray tsrcPixArray, jint tw, jint th, jint flag)
{
	jint* srcPix = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* tPix = (*env)->GetIntArrayElements(env, tsrcPixArray, 0);

	HalfTone(srcPix, w, h,tPix, tw, th, flag);

	(*env)->ReleaseIntArrayElements(env, srcPixArray, srcPix, 0);
	(*env)->ReleaseIntArrayElements(env, tsrcPixArray, tPix, 0);
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_CLAHERGB3
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint block_num, jint edg_thre)
 {
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	CLAHERGB3(pixels,w,h,block_num, edg_thre);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

 }
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_LocaEnhanceRGB
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint block_num, jint edg_thre)
 {
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	LocaEnhanceRGB(pixels,w,h,block_num, edg_thre);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

 }
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_HDR
 (JNIEnv *env, jclass obj, jintArray dark, jintArray bright, jintArray dst, jint w, jint h)
 {
 	jint* imgDark = (*env)->GetIntArrayElements(env, dark, 0);

 	jint* imgBright = (*env)->GetIntArrayElements(env, bright, 0);

 	jint* imgDst = (*env)->GetIntArrayElements(env, dst, 0);
 	HDR(imgDark,imgBright,imgDst,w,h);

 	(*env)->ReleaseIntArrayElements(env, dark, imgDark, 0);
 	(*env)->ReleaseIntArrayElements(env, bright, imgBright, 0);
 	(*env)->ReleaseIntArrayElements(env, dst, imgDst, 0);

 }

JNIEXPORT jint JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_ColorTemperature
 (JNIEnv *env, jclass obj, jintArray img, jintArray dst,  jint size, jint temperature)
{
 	jint* src_pixels = (*env)->GetIntArrayElements(env, img, 0);
 	jint* dst_pixels = (*env)->GetIntArrayElements(env, dst, 0);

 	jint t = ColorTemperature(src_pixels,dst_pixels, size, temperature);

 	(*env)->ReleaseIntArrayElements(env, img, src_pixels, 0);
 	(*env)->ReleaseIntArrayElements(env, dst, dst_pixels, 0);

 	return t;
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_autoContrast
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jfloat thre_low, jfloat thre_high)
{
 	jint* src_pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);

 	autoContrast(src_pixels,w,h,thre_low,thre_high);

 	(*env)->ReleaseIntArrayElements(env, srcPixArray, src_pixels, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_ShadowHighLight
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
 	jint* src_pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);

 	ShadowHighLight(src_pixels,w,h);

 	(*env)->ReleaseIntArrayElements(env, srcPixArray, src_pixels, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_ShadowHighLight1
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
 	jint* src_pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);

 	ShadowHighLight1(src_pixels,w,h);

 	(*env)->ReleaseIntArrayElements(env, srcPixArray, src_pixels, 0);
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_ShadowHighLight2
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
 	jint* src_pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);

 	ShadowHighLight2(src_pixels,w,h);

 	(*env)->ReleaseIntArrayElements(env, srcPixArray, src_pixels, 0);
}
//JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_NightImageInput
// (JNIEnv *env, jclass obj, jintArray src, jintArray dst,  jint w,  jint h, jint img_total, jint img_id)
//{
// 	jint* src_pixels = (*env)->GetIntArrayElements(env, src, 0);
// 	jint* dst_pixels = (*env)->GetIntArrayElements(env, dst, 0);
//
//// 	LOGW("nightInput: w: %d  h: %d ", w, h);
//
// 	NightImageInput(src_pixels,dst_pixels, w, h, img_total, img_id);
//
// 	(*env)->ReleaseIntArrayElements(env, src, src_pixels, 0);
// 	(*env)->ReleaseIntArrayElements(env, dst, dst_pixels, 0);
//}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_NightGenerateYUV
(JNIEnv *env, jclass obj, jobjectArray  srcImages, jintArray dst,
		jint w_src,  jint h_src, jint w_dst,  jint h_dst, jint img_total)
{

 	jint i, j;

 	int row = (*env)->GetArrayLength(env, srcImages);//鑾峰緱琛屾暟

 	jarray srcImgArray = ((*env)->GetObjectArrayElement(env, srcImages, 0));

 	int col =(*env)->GetArrayLength(env, srcImgArray); //鑾峰緱鍒楁暟

 	LOGW("night get byte data size : %d", row);

 	jbyte *src_pixels[row];

 	for(i = 0; i<row; i++){
 		srcImgArray = ((*env)->GetObjectArrayElement(env, srcImages, i));
 		src_pixels[i] = (*env)->GetByteArrayElements(env, (jbyteArray)srcImgArray, 0 );

 	}

 	jint* dst_pixels = (*env)->GetIntArrayElements(env, dst, 0);

// 	NightGenerateYUV(src_pixels,dst_pixels, w_src, h_src, w_dst, h_dst, img_total);


 	for(i = 0; i<row; i++){
 	 		srcImgArray = ((*env)->GetObjectArrayElement(env, srcImages, i));
 	 		(*env)->ReleaseByteArrayElements(env, (jbyteArray)srcImgArray, src_pixels[i],0 );
 	 }

 	(*env)->ReleaseIntArrayElements(env, dst, dst_pixels, 0);
}



JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_NightGenerate
(JNIEnv *env, jclass obj, jobjectArray  srcImages, jintArray dst,  jint w,  jint h, jint img_total)
{

//	LOGW("ok\n");

 	//jint* src_pixels = (*env)->GetIntArrayElements(env, src, 0);

// 	jint** src_pixels = (jint**)malloc(img_total*sizeof(jint*));

 	jint i, j;

// 	LOGW("nightGenerate: w: %d, h: %d", w, h);

 	int row = (*env)->GetArrayLength(env, srcImages);//鑾峰緱琛屾暟

// 	LOGW("nightGenerate: row: %d", row);

 	jarray srcImgArray = ((*env)->GetObjectArrayElement(env, srcImages, 0));

 	int col =(*env)->GetArrayLength(env, srcImgArray); //鑾峰緱鍒楁暟

// 	LOGW("nightGenerate: col: %d", col);

 	jint *src_pixels[row];

 	for(i = 0; i<row; i++){
// 		LOGW("nightGenerate: write row: %d", row);
 		srcImgArray = ((*env)->GetObjectArrayElement(env, srcImages, i));
 		src_pixels[i] = (*env)->GetIntArrayElements(env, (jintArray)srcImgArray, 0 );
// 		for(j=0; j<col; j++){
// 			src_pixels[i][j] = coldata[j];//鍙栧嚭JAVA绫讳腑arrayData鐨勬暟鎹�骞惰祴鍊肩粰JNI涓殑鏁扮粍
// 		}

// 		(*env)->ReleaseIntArrayElements(env, (jintArray)srcImgArray, coldata,0 );
 	}

// 	LOGW("nightGenerate: write dst");
 	jint* dst_pixels = (*env)->GetIntArrayElements(env, dst, 0);

 	NightGenerate(src_pixels,dst_pixels, w, h, img_total);


 	for(i = 0; i<row; i++){
 	// 		LOGW("nightGenerate: release: %d", row);
 	 		srcImgArray = ((*env)->GetObjectArrayElement(env, srcImages, i));
 	 		(*env)->ReleaseIntArrayElements(env, (jintArray)srcImgArray, src_pixels[i],0 );
 	 }

 	(*env)->ReleaseIntArrayElements(env, dst, dst_pixels, 0);
}



JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_deHaze
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint level, jfloat Rat, jfloat RatE, jfloat RatL)
{
	jint *pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	deHaze(pixels, w, h, level, Rat, RatE, RatL);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_sceneProcess
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint *pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	sceneProcess(pixels, w, h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_sceneEnhance
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint para1, jint para2, jintArray LAB)
{
	jint *pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint *labPixels = (*env)->GetIntArrayElements(env, LAB, 0);
//	jint *labPixels2 = (*env)->GetIntArrayElements(env, LABb, 0);
	sceneEnhance(pixels, w, h, para1, para2, labPixels);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, LAB, labPixels, 0);
//	(*env)->ReleaseIntArrayElements(env, LABb, labPixels2, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_Bilateral
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint delta, jint radius, jint sigma)
{
	jint *pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	Bilateral(pixels, w, h, delta, radius, sigma);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_equlizeHist
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	equlizeHist(pixels,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}


 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_releaseSource
(JNIEnv *env, jclass obj, jintArray srcPixArray)
{
//	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	releaseSource();
//	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_produceArea
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray baseflag, jint w, jint h, jintArray modifyColor)
 {
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* flagArray = (*env)->GetIntArrayElements(env, baseflag, 0);
	jint* mcolor = (*env)->GetIntArrayElements(env, modifyColor, 0);
	produceArea(pixels,flagArray, w, h, mcolor);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, baseflag, flagArray, 0);
	(*env)->ReleaseIntArrayElements(env, modifyColor, mcolor, 0);
 }

 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_InitializeCircle
 (JNIEnv *env, jclass obj, jint eyex1, jint eyey1, jint eyeradius1, jint eyex2, jint eyey2, jint eyeradius2, jintArray srcPixArray, jint w, jint h, jint ratio)
 {
	 jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	 InitializeCircle(eyex1, eyey1, eyeradius1, eyex2, eyey2, eyeradius2, pixels, w, h, ratio);
	 (*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
 }

 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_usmProcess
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint radius, jint thres, jint amount)
 {
 	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
 	usmProcess(pixels,w,h, radius, thres, amount);
 	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

 }

 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_BrightEyes
 (JNIEnv *env, jclass obj, jintArray srcPixArray,jint w, jint h,jint ratio, jint leftX,jint leftY,jint rightX,jint rightY)
 {
	 jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	 BrightEyes(pixels, w, h, ratio, leftX, leftY, rightX, rightY);
	 (*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
 }

 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_backLight
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint leftX, jint leftY, jint rightX, jint rightY, jint sAmount, jint lAmount, jintArray darkerTable, jintArray lighterTable)
 {
	 jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	 jint* realDarkerTable = (*env)->GetIntArrayElements(env, darkerTable, 0);
	 jint* realLighterTable = (*env)->GetIntArrayElements(env, lighterTable, 0);
	 backLight(pixels, w, h, leftX, leftY, rightX, rightY, sAmount, lAmount, realDarkerTable, realLighterTable);
	 (*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	 (*env)->ReleaseIntArrayElements(env, darkerTable, realDarkerTable, 0);
	 (*env)->ReleaseIntArrayElements(env, lighterTable, realLighterTable, 0);
 }

 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_whiten
 (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint ratio, jint sAxis, jint lAxis, jint centerX, jint centerY)
 {
	 jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	 whiten(pixels,w,h, ratio, sAxis, lAxis, centerX, centerY);
	 (*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
 }

// JNIEXPORT jint JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_LightenDemo
//(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint sAxis, jint lAxis, jint centerX, jint centerY)
//{
//	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
//	jint r = LightenDemo(pixels, w, h, sAxis, lAxis, centerX, centerY);
//	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
//	return r;
//}

 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_faceBuffing
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jintArray R_Table, jintArray G_Table, jintArray B_Table)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* R = (*env)->GetIntArrayElements(env, R_Table, 0);
	jint* G = (*env)->GetIntArrayElements(env, G_Table, 0);
	jint* B = (*env)->GetIntArrayElements(env, B_Table, 0);
	faceBuffing(pixels, w, h, R ,G, B, 100);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, R_Table, R, 0);
	(*env)->ReleaseIntArrayElements(env, G_Table, G, 0);
	(*env)->ReleaseIntArrayElements(env, B_Table, B, 0);
}


 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_faceBuffingBackup
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jintArray R_Table, jintArray G_Table, jintArray B_Table)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* R = (*env)->GetIntArrayElements(env, R_Table, 0);
	jint* G = (*env)->GetIntArrayElements(env, G_Table, 0);
	jint* B = (*env)->GetIntArrayElements(env, B_Table, 0);
	faceBuffing(pixels, w, h, R ,G, B, 100);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, R_Table, R, 0);
	(*env)->ReleaseIntArrayElements(env, G_Table, G, 0);
	(*env)->ReleaseIntArrayElements(env, B_Table, B, 0);
}

 JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_faceBuffingWeight
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jintArray R_Table, jintArray G_Table, jintArray B_Table, jint weight)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* R = (*env)->GetIntArrayElements(env, R_Table, 0);
	jint* G = (*env)->GetIntArrayElements(env, G_Table, 0);
	jint* B = (*env)->GetIntArrayElements(env, B_Table, 0);
	faceBuffing(pixels, w, h, R ,G, B, weight);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, R_Table, R, 0);
	(*env)->ReleaseIntArrayElements(env, G_Table, G, 0);
	(*env)->ReleaseIntArrayElements(env, B_Table, B, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_SmoothSkinProcessor_buffingTemplate
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint bb, jint flag)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	buffingTemplate(pixels,w,h, bb, flag);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_normalization
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint clipShadow, jint clipHighlight)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	normalization(pixels,w,h,clipShadow, clipHighlight);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_autoContrast2
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint clipShadow, jint clipHighlight)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	autoContrast2(pixels,w,h,clipShadow, clipHighlight);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_autoColor
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint clipShadow, jint clipHighlight)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	autoColor(pixels,w,h,clipShadow, clipHighlight);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_brightEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint bb)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	brightEffect(pixels,w,h,bb);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_contrastEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h,jint bb)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	contrastEffect(pixels,w,h,bb);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}



JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_eyeEnlarge
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h,jint x, jint y, jint r, jfloat scale)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	eyeEnlarge(pixels,w,h,x,y,r,scale);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_eyeEnlargeWithTags
(JNIEnv *env, jclass obj, jintArray inp, jintArray outp, jint w, jint h,jintArray xp, jintArray yp, jintArray rp, jfloatArray scalep, jint num)
{
	jint* in = (*env)->GetIntArrayElements(env, inp, 0);
	jint* out = (*env)->GetIntArrayElements(env, outp, 0);
	jint* x = (*env)->GetIntArrayElements(env, xp, 0);
	jint* y = (*env)->GetIntArrayElements(env, yp, 0);
	jint* r = (*env)->GetIntArrayElements(env, rp, 0);
	jfloat* scale = (*env)->GetFloatArrayElements(env, scalep, 0);
	eyeEnlargeWithTags(in, out, w, h, x, y, r, scale, num);
	(*env)->ReleaseIntArrayElements(env, inp, in, 0);
	(*env)->ReleaseIntArrayElements(env, outp, out, 0);
	(*env)->ReleaseIntArrayElements(env, xp, x, 0);
	(*env)->ReleaseIntArrayElements(env, yp, y, 0);
	(*env)->ReleaseIntArrayElements(env, rp, r, 0);
	(*env)->ReleaseFloatArrayElements(env, scalep, scale, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_eyeBrighten
  (JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint bb)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	eyeBrighten(pixels,w,bb);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_curveEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray tranr,jintArray trang,jintArray tranb, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* transr = (*env)->GetIntArrayElements(env, tranr, 0);
	jint* transg = (*env)->GetIntArrayElements(env, trang, 0);
	jint* transb = (*env)->GetIntArrayElements(env, tranb, 0);
	curve(pixels,transr,transg,transb,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, tranr, transr, 0);
	(*env)->ReleaseIntArrayElements(env, trang, transg, 0);
	(*env)->ReleaseIntArrayElements(env, tranb, transb, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_blueEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray tranr,jintArray trang,jintArray tranb, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* transr = (*env)->GetIntArrayElements(env, tranr, 0);
	jint* transg = (*env)->GetIntArrayElements(env, trang, 0);
	jint* transb = (*env)->GetIntArrayElements(env, tranb, 0);
	blue(pixels,transr,transg,transb,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, tranr, transr, 0);
	(*env)->ReleaseIntArrayElements(env, trang, transg, 0);
	(*env)->ReleaseIntArrayElements(env, tranb, transb, 0);
}

/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_screenEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	Screen(pixels,pixelsLayer,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

/*Author: huhu*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_alphaCompositeEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint alpha)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	AlphaComposite(pixels,pixelsLayer,w,h,alpha);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);

}

/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_overlayEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	Overlay(pixels,pixelsLayer,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_rsOverlayEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint pw, jint ph)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	RSOverlay(pixels,pixelsLayer,w,h,pw,ph);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_overlayAlphaEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint alpha)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	OverlayAlpha(pixels,pixelsLayer,w,h,alpha);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_rsOverlayAlphaEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint pw, jint ph, jint alpha)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	RSOverlayAlpha(pixels,pixelsLayer,w,h,pw,ph,alpha);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_rsCoverageEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint pw, jint ph)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	RSCoverage(pixels,pixelsLayer,w,h,pw,ph);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_rsMultiplyAlphaEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint pw, jint ph, jint alpha)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	RSMultiplyAlpha(pixels,pixelsLayer,w,h,pw,ph,alpha);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_multiplyAlphaEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint alpha)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	MultiplyAlpha(pixels,pixelsLayer,w,h,alpha);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_multiplyEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	Multiply(pixels,pixelsLayer,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_rsMultiplyEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint pw, jint ph)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	RSMultiply(pixels,pixelsLayer,w,h,pw,ph);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_lineardodgeEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	LinearDodge(pixels,pixelsLayer,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_gray
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	gray(pixels,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_blur
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint r)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	blur(pixels,w,h,x,y,r);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_dlomo
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint bound)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	dlomo(pixels,w,h,x,y,bound);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_lomo
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint color, jint brightRatio, jint darkRatio, jint distScope)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	lomo(pixels,w,h, color, brightRatio, darkRatio, distScope);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_llomo
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint bound)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	llomo(pixels,w,h,x,y,bound);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_smoothEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	smoothEffect(pixels,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_singleColorEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h,jfloatArray matrix,jfloat targetRed,jfloat targetGreen,jfloat targetBlue,jfloat threshold,jfloat maxup)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jfloat* mat = (*env)->GetFloatArrayElements(env, matrix, 0);

	singleColor(pixels,w,h,mat,targetRed,targetGreen,targetBlue,threshold,maxup);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseFloatArrayElements(env, srcPixArray, mat, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_skinWhitePointEffect
(JNIEnv *env, jclass obj, jintArray oldPixArray, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint r)
{
    jint* oldPixels = (*env)->GetIntArrayElements(env,  oldPixArray, 0);
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	skinWhitePointEffect(oldPixels,pixels, w, h, x, y, r);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, oldPixArray, oldPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_skinWhiteTeethPointEffect
(JNIEnv *env, jclass obj, jintArray oldPixArray, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint r)
{
    jint* oldPixels = (*env)->GetIntArrayElements(env,  oldPixArray, 0);
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	skinWhiteTeethPointEffect(oldPixels,pixels, w, h, x, y, r);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, oldPixArray, oldPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_skinSmoothPointEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint r)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	skinSmoothPointEffect(pixels, w, h, x, y, r);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_thinEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint x2, jint y2, jint r, jfloat scale, jint flag)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	thinEffect(pixels, w, h, x, y, x2, y2, r, scale, flag);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_thinEffectAuto
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint x2, jint y2, jint degree)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	thinEffectAuto(pixels, w, h, x, y, x2, y2, degree);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_thinEffectWholeFace
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint faceLeft, jint faceRight, jint faceTop, jint faceBottom, jfloat scale)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	thinEffectWholeFace(pixels, w, h, faceLeft, faceRight, faceTop, faceBottom, scale);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_smileWholeMouth
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint faceLeft, jint faceRight, jint faceTop, jint faceBottom, jfloat scale)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	smileWholeMouth(pixels, w, h, faceLeft, faceRight, faceTop, faceBottom, scale);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_gaussBlur
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint radius, jfloat sigma)
{

	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	gaussBlur(pixels,w,h,radius,sigma);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_dreamy
(JNIEnv *env, jclass obj,jintArray srcPixArray, jint w, jint h, jint radius)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	dreamy(pixels,w,h,radius);
	//fastAverageBlur(pixels, w, h, radius);
	//averageBlur(pixels, w, h, radius);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}


/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_softlightEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	SoftLight(pixels,pixelsLayer,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_darkenEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	Darken(pixels,pixelsLayer,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

/*Author: mimi*/
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_lightenEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	Lighten(pixels,pixelsLayer,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_autoLevels
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
    jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	autoLevels(pixels,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_redeyeEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint x, jint y, jint radius)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	redeye(pixels,w,h,x, y,radius);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_getDynamicFrame
(JNIEnv *env, jclass obj, jintArray frame, jintArray oriFrame, jint w, jint h, jint oriW, jint oriH)
{
    jint* pixels = (*env)->GetIntArrayElements(env, frame, 0);
    jint* oriPixels = (*env)->GetIntArrayElements(env, oriFrame, 0);
	getDynamicFrame(pixels, oriPixels, w, h, oriW, oriH);
	(*env)->ReleaseIntArrayElements(env, frame, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, oriFrame, oriPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_setVisibleArea
(JNIEnv *env, jclass obj, jintArray pixelsArray, jint w, jint h, jintArray vertexsArray, jint cnt)
{
	jint* pixels = (*env)->GetIntArrayElements(env, pixelsArray, 0);
	jint* vertexs;
	if(cnt == 0)
	{
		vertexs = 0;
	}
	else
	{
		vertexs = (*env)->GetIntArrayElements(env, vertexsArray, 0);
	}
	setVisibleArea(pixels, w, h, vertexs, cnt);
	(*env)->ReleaseIntArrayElements(env, pixelsArray, pixels, 0);
	if(vertexs)
	{
		(*env)->ReleaseIntArrayElements(env, vertexsArray, vertexs, 0);
	}
}
JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_coverEffect
(JNIEnv *env, jclass obj, jintArray pixel, jintArray layerPixel, jint w, jint h)
{
	jint* basePixels = (*env)->GetIntArrayElements(env, pixel, 0);
	jint* layerPixels = (*env)->GetIntArrayElements(env, layerPixel, 0);
	cover(basePixels, layerPixels, w, h);
	(*env)->ReleaseIntArrayElements(env, pixel, basePixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerPixel, layerPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_sketchEffect
(JNIEnv *env, jclass obj, jintArray pixel, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, pixel, 0);
	sketch(pixels, w, h);
	(*env)->ReleaseIntArrayElements(env, pixel, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_popstyle
(JNIEnv *env, jclass obj, jintArray pixel, jint w, jint h,int type)
{
	jint* pixels = (*env)->GetIntArrayElements(env, pixel, 0);
	popstyle(pixels, w, h,type);
	(*env)->ReleaseIntArrayElements(env, pixel, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_blurBackgroundEffectByCircle
(JNIEnv *env, jclass obj, jintArray pixel, jint w, jint h, jint x, jint y, jint r0, jint r1)
{
	jint* pixels = (*env)->GetIntArrayElements(env, pixel, 0);
	blurBackgroundByCircle(pixels, w, h , x ,y, r0, r1);
	(*env)->ReleaseIntArrayElements(env, pixel, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_blurBackgroundEffectByLine
(JNIEnv *env, jclass obj, jintArray pixel, jint w, jint h, jint x, jint y, jint r0, jint r1, jfloat theta)
{
	LOGW("jni blur theta: %f", theta);
	jint* pixels = (*env)->GetIntArrayElements(env, pixel, 0);
	blurBackgroundByLine(pixels, w, h , x ,y, r0, r1, theta);
	(*env)->ReleaseIntArrayElements(env, pixel, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_unsharpEffect
(JNIEnv *env, jclass obj, jintArray pixel, jintArray smoothImg,jint w, jint h , jint rad , jint thresh , jfloat amount)
{
	jint* pixels = (*env)->GetIntArrayElements(env, pixel, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, smoothImg, 0);
	unsharp(pixels,pixelsLayer, w, h , rad ,thresh, amount);
	(*env)->ReleaseIntArrayElements(env, pixel, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, smoothImg, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_sharpenEffect
(JNIEnv *env, jclass obj, jintArray pixel,jint w, jint h , jint r)
{
	jint* pixels = (*env)->GetIntArrayElements(env, pixel, 0);
	sharpen(pixels, w, h, r);
	(*env)->ReleaseIntArrayElements(env, pixel, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_fastAverageBlur
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint radius)
{

	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	fastAverageBlur(pixels,w,h,radius);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_relief
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint increment)
{

	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	relief(pixels,w,h,increment);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_softlightEffectEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	SoftLight(pixels,pixelsLayer,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_emissionEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	emission(pixels,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_etocEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	etoc(pixels,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_postivefilterEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	postivefilter(pixels,w,h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_colorLevel
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint min, jfloat gray, jint max, jint outMin, jint outMax)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	colorLevelFilter(pixels, w, h, min, gray, max, outMin, outMax);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_colorBurn
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* layerPixels = (*env)->GetIntArrayElements(env, layerPixArray, 0);
	ColorBurn(pixels, layerPixels, w, h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerPixArray, layerPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_linearBurn
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerPixArray, jint w, jint h, jint layeralpha)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* layerPixels = (*env)->GetIntArrayElements(env, layerPixArray, 0);
	LOGW("layeralpha: %d", layeralpha);
	LinearBurn(pixels, layerPixels, w, h, layeralpha);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerPixArray, layerPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_rsLinearBurn
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerArray, jint w, jint h, jint pw, jint ph, jint layeralpha)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* pixelsLayer = (*env)->GetIntArrayElements(env, layerArray, 0);
	RSLinearBurn(pixels,pixelsLayer,w,h,pw,ph,layeralpha);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerArray, pixelsLayer, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_darken
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerPixArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* layerPixels = (*env)->GetIntArrayElements(env, layerPixArray, 0);
	Darken(pixels, layerPixels, w, h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerPixArray, layerPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_screenWithLimitedLayer
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerPixArray, jint w, jint h, jint lw, jint lh)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* layerPixels = (*env)->GetIntArrayElements(env, layerPixArray, 0);
	ScreenWithLimitedLayer(pixels, layerPixels, w, h, lw, lh);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerPixArray, layerPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_rsScreenEffect
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerPixArray, jint w, jint h, jint lw, jint lh)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* layerPixels = (*env)->GetIntArrayElements(env, layerPixArray, 0);
	RSScreenWithLimitedLayer(pixels, layerPixels, w, h, lw, lh);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerPixArray, layerPixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_mergeSelection
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerPixArray, jintArray selArray, jint w, jint h)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* layerPixels = (*env)->GetIntArrayElements(env, layerPixArray, 0);
	jint* sel = (*env)->GetIntArrayElements(env, selArray, 0);
	MergeSelection(pixels, layerPixels, sel, w, h);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerPixArray, layerPixels, 0);
	(*env)->ReleaseIntArrayElements(env, selArray, sel, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_mergeWeight
(JNIEnv *env, jclass obj, jintArray srcPixArray, jintArray layerPixArray, jint w, jint h, jint weight)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* layerPixels = (*env)->GetIntArrayElements(env, layerPixArray, 0);
	MergeWeight(pixels, layerPixels, w, h, weight);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
	(*env)->ReleaseIntArrayElements(env, layerPixArray, layerPixels, 0);

}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_saturation
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jfloat svalue)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	saturationfilter(pixels, w, h, svalue);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_fastAverageBlurWithThreshold
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, int radius, int threshold)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	LOGW("fastAverageBlurWithThreshold: w: %d  h: %d  r: %d  threshold: %d", w, h,
		radius, threshold)
;
	fastAverageBlurWithThreshold(pixels, w, h, radius, threshold);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_fastAverageBlurWithThresholdAndWeight
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, int radius, int threshold, int weight)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
//	LOGW("fastAverageBlurWithThreshold: w: %d  h: %d  r: %d  threshold: %d", w, h,
//		radius, threshold, weight)
//;
	fastAverageBlurWithThresholdAndWeight(pixels, w, h, radius, threshold, weight);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_fastAverageBlurWithThresholdWeightSkinDetection
(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint h, jint radius, jint threshold, jint weight,
		jint hmin, jint hmax, jint smin, jint smax, jint vmin, jint vmax)
{
	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
//	LOGW("fastAverageBlurWithThreshold: w: %d  h: %d  r: %d  threshold: %d", w, h,
//		radius, threshold, weight)
//;
fastAverageBlurWithThresholdWeightSkinDetection(pixels, w, h, radius, threshold, weight, hmin, hmax, smin, smax, vmin, vmax);
(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_yuv420sp2rgb
(JNIEnv *env, jclass obj, jbyteArray rgb, jbyteArray yuv420sp, jint w, jint h)
{
	jbyte* pixels = (*env)->GetByteArrayElements(env, rgb, 0);
	jbyte* yuv = (*env)->GetByteArrayElements(env, yuv420sp, 0);
	color_convert_common(yuv, yuv + w * h, w, h, pixels);
	(*env)->ReleaseByteArrayElements(env, rgb, pixels, 0);
	(*env)->ReleaseByteArrayElements(env, yuv420sp, yuv, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_yuv2rgbResize(
	JNIEnv* env, jobject object, jbyteArray pinArray, jint inWidth,
	jint inHeight, jbyteArray poutArray, jint outWidth, jint outHeight, jint direction)
{
	jbyte* yuv = (*env)->GetByteArrayElements(env, pinArray, 0);
	jbyte* out = (*env)->GetByteArrayElements(env, poutArray, 0);
	yuv2rgbResize(yuv, yuv + inWidth * inHeight, inWidth, inHeight, out, outWidth, outHeight);
	rgbRotate(out, outWidth, direction);
	(*env)->ReleaseByteArrayElements(env, pinArray, yuv, 0);
	(*env)->ReleaseByteArrayElements(env, poutArray, out, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_yuv2rgbBitmap(
	JNIEnv* env, jobject object, jbyteArray pinArray, jint inWidth,
	jint inHeight, jintArray pixels)
{
	jbyte* yuv = (*env)->GetByteArrayElements(env, pinArray, 0);
	jint* out = (*env)->GetIntArrayElements(env, pixels, 0);
	yuv2rgbBitmap(yuv, yuv + inWidth * inHeight, inWidth, inHeight, out);
	(*env)->ReleaseByteArrayElements(env, pinArray, yuv, 0);
	(*env)->ReleaseIntArrayElements(env, pixels, out, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_transToGray
(JNIEnv *env, jclass obj, jintArray srcPixArray, jbyteArray dstPixArray, jint w, jint h)
{
jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
jbyte* dst = (*env)->GetByteArrayElements(env, dstPixArray, 0);
transToGray(pixels, w, h, dst);
(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
(*env)->ReleaseByteArrayElements(env, dstPixArray, dst, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_transToReversedBGR
(JNIEnv *env, jclass obj, jintArray srcPixArray, jbyteArray dstPixArray, jint w, jint h)
{
jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
jbyte* dst = (*env)->GetByteArrayElements(env, dstPixArray, 0);
transReversedBGR(pixels, w, h, dst);
(*env)->ReleaseIntArrayElements(env, srcPixArray, pixels, 0);
(*env)->ReleaseByteArrayElements(env, dstPixArray, dst, 0);
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_progressiveLineInitialize
(JNIEnv *env, jclass obj, jint r, jint w, jint h, jint times, jint channel)
{
	lineInitialize(r, w, h, times, channel);
}

JNIEXPORT jintArray JNICALL Java_cn_jingling_lib_filters_CMTProcessor_progressiveLineProcess(
	JNIEnv *env, jclass obj, jintArray srcPixArray, jint w)
{
	jint* srcpixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
	jint* dstpixels = lineProcess(srcpixels);
	(*env)->ReleaseIntArrayElements(env, srcPixArray, srcpixels, 0);
	if (NULL == dstpixels) {
		return NULL;
	} else {
		jintArray dstPixArray = (*env)->NewIntArray(env, w);
		(*env)->SetIntArrayRegion(env, dstPixArray, 0, w, dstpixels);
		return dstPixArray;
	}
}

JNIEXPORT void JNICALL Java_cn_jingling_lib_filters_CMTProcessor_progressiveRelease
(JNIEnv *env, jclass obj)
{
	sourceRelease();
}

//JNIEXPORT jintArray JNICALL Java_cn_jingling_lib_filters_CMTProcessor_usmProcessProgressive
//(JNIEnv *env, jclass obj, jintArray srcPixArray, jint w, jint radius, jint thres, jint amount)
//{
////	jint* pixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
//	jint* srcpixels = (*env)->GetIntArrayElements(env, srcPixArray, 0);
//	jint* dstpixels = usmProcessProgressive(srcpixels, w, radius, thres, amount);
//	(*env)->ReleaseIntArrayElements(env, srcPixArray, srcpixels, 0);
//	if (NULL == dstpixels) {
//		return NULL;
//	} else {
//		jintArray dstPixArray = (*env)->NewIntArray(env, w);
//		(*env)->SetIntArrayRegion(env, dstPixArray, 0, w, dstpixels);
//		return dstPixArray;
//	}
//}

