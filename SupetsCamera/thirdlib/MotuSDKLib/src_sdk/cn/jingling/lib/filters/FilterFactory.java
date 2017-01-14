package cn.jingling.lib.filters;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import cn.jingling.lib.PackageSecurity;
import cn.jingling.lib.filters.global.BetterSkin;
import cn.jingling.lib.filters.global.Brightness;
import cn.jingling.lib.filters.global.ColorTemperature;
import cn.jingling.lib.filters.global.Contrast;
import cn.jingling.lib.filters.global.EyeEnlargeAuto;
import cn.jingling.lib.filters.global.LomoDrag;
import cn.jingling.lib.filters.global.Saturation;
import cn.jingling.lib.filters.global.Sharpen;
import cn.jingling.lib.filters.global.SmoothSkin;
import cn.jingling.lib.filters.global.TestSkin;
import cn.jingling.lib.filters.global.Viberation;
import cn.jingling.lib.filters.onekey.*;
import cn.jingling.lib.filters.partial.BackGroundBlurLine;
import cn.jingling.lib.filters.partial.BackGroundBlurRound;
import cn.jingling.lib.filters.partial.BlackEyeRemove;
import cn.jingling.lib.filters.partial.Crop;
import cn.jingling.lib.filters.partial.EyeEnlarge;
import cn.jingling.lib.filters.partial.RedEyeRemove;
import cn.jingling.lib.filters.partial.Thin;
import cn.jingling.lib.filters.partial.WhelkRemove;
import cn.jingling.lib.filters.partial.WhelkRemoveTest2;
import cn.jingling.lib.filters.realsize.RSAnsel;
import cn.jingling.lib.filters.realsize.RSCameraCaiSeFuPianLive;
import cn.jingling.lib.filters.realsize.RSCameraDiana;
import cn.jingling.lib.filters.realsize.RSCameraDushiLive;
import cn.jingling.lib.filters.realsize.RSCameraFoodLive1;
import cn.jingling.lib.filters.realsize.RSCameraFoodLive2;
import cn.jingling.lib.filters.realsize.RSCameraFoodLive3;
import cn.jingling.lib.filters.realsize.RSCameraFoodLive4;
import cn.jingling.lib.filters.realsize.RSCameraFoodLive5;
import cn.jingling.lib.filters.realsize.RSCameraFoodLive6;
import cn.jingling.lib.filters.realsize.RSCameraFuguLive;
import cn.jingling.lib.filters.realsize.RSCameraFuguSceneryLive;
import cn.jingling.lib.filters.realsize.RSCameraG3Live;
import cn.jingling.lib.filters.realsize.RSCameraGoldFinchLive;
import cn.jingling.lib.filters.realsize.RSCameraGuangyinLive;
import cn.jingling.lib.filters.realsize.RSCameraHefeLive;
import cn.jingling.lib.filters.realsize.RSCameraHeibaiLive;
import cn.jingling.lib.filters.realsize.RSCameraHuiyi;
import cn.jingling.lib.filters.realsize.RSCameraJiuguanLive;
import cn.jingling.lib.filters.realsize.RSCameraLiunian;
import cn.jingling.lib.filters.realsize.RSCameraLomoLive;
import cn.jingling.lib.filters.realsize.RSCameraLomoSceneryLive;
import cn.jingling.lib.filters.realsize.RSCameraLouguangLive;
import cn.jingling.lib.filters.realsize.RSCameraM3Live;
import cn.jingling.lib.filters.realsize.RSCameraMeadowLive;
import cn.jingling.lib.filters.realsize.RSCameraMidwayLive;
import cn.jingling.lib.filters.realsize.RSCameraQiuse;
import cn.jingling.lib.filters.realsize.RSCameraRiseLive;
import cn.jingling.lib.filters.realsize.RSCameraSceneEnhance;
import cn.jingling.lib.filters.realsize.RSCameraSutroLive;
import cn.jingling.lib.filters.realsize.RSCameraVividLive;
import cn.jingling.lib.filters.realsize.RSCameraWaldenLive;
import cn.jingling.lib.filters.realsize.RSCameraXuancai;
import cn.jingling.lib.filters.realsize.RSCameraYanliLive;
import cn.jingling.lib.filters.realsize.RSCameraYazhiLive;
import cn.jingling.lib.filters.realsize.RSCameraZaoanLive;
import cn.jingling.lib.filters.realsize.RSCounterRotate;
import cn.jingling.lib.filters.realsize.RSDecolorization;
import cn.jingling.lib.filters.realsize.RSEmptyFilter;
import cn.jingling.lib.filters.realsize.RSFlipHorizontal;
import cn.jingling.lib.filters.realsize.RSRotate;
import cn.jingling.lib.filters.realsize.RSTestYBW;
import cn.jingling.lib.livefilter.LiveSkinDetect;
import cn.jingling.lib.utils.ErrorHandleHelper;


public class FilterFactory {
	private static String GET_FILTER_TAG = "FilterFactory";
	private static String GET_FILTER_ERROR_MSG = "Filter Instantiation Error: Can't find such filter! Please check the correctness of your label!";
	
	private static final Map<String, Class<? extends OneKeyFilter>> sOneKeyFilters = new HashMap<String, Class<? extends OneKeyFilter>> () {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4215192030410560547L;

		{
//			put("enhancebackup", SceneEnhanceBackup.class );

			put("hope", HopeEffect.class);
			put("halftone", HalfTone.class);
			put("halftonegray", HalfToneGray.class);
			put("shadowandhighlight", shadowAndHighlight.class);
			put("bmopi", BEEPS.class);
			put("enhance1", SceneEnhance.class);
			put("msrcr", Msrcr.class);
			put("sceneprocess", SceneProcess.class);
			put("dehaze", DeHaze.class);
			put("bilateral", Bilateral.class);
			put("denoise", Denoise.class);
			put("backlight", BackLight.class);
			put("lighteneye", LightenEye.class);
			put("artificial", BlackEyeArtificial.class);
			put("whiten", whiten.class);
			put("whitenmopi", whitenMopi.class);
			put("usm", usmProcess.class);
			put("lightenmopi", Lighten.class);
			put("blackeye", BlackEye.class);
			put("blackeyeruo", BlackEyeRuo.class);

			//put("facedetail", FaceDetailTest.class);
			put("clomo", CameraLomo.class);
			put("cliunian", CameraLiunian.class);
			put("cxuancai", CameraXuancai.class);
			put("cdiana", CameraDiana.class);
			put("cfugu", CameraFugu.class);
			put("chuiyi", CameraHuiyi.class);
			
			put("dianapath", DianaPath.class);//Diana哥特
			put("autumn", Autumn.class);//秋色
			put("nashiv", Nashiv.class);//复古
			put("instantpath", InstantPath.class);//回忆

			put("ctianmei", CameraTianmei.class);
			put("cwennuan", CameraWennuan.class);
			put("crixi", CameraRixi.class);
			put("cyouya", CameraYouya.class);

			put("eyebrighten", EyeBrighten.class);
			put("facebuffinggauss", FaceBuffingGauss.class);
			put("facebuffingfft", FaceBuffingFFT.class);
			put("lomocode", LomoCode.class);
			put("cfoodfine", CameraFoodFine.class);
			put("cfoodfresh", CameraFoodFresh.class);
			put("cfoodhealthy", CameraFoodHealthy.class);
			put("cfoodtasty", CameraFoodTasty.class);
			put("cfoodyum", CameraFoodYum.class);
			put("cself", CameraSelf.class);
			put("naturalwhite", CameraNaturalWhite.class);
			put("deepwhite", CameraMeibai.class);
			put("cxianhuo", CameraXianhuo.class);
			put("cshenchen", CameraShenchen.class);
			put("holga", CameraHolga.class);
			put("tianmei", TianMei.class);
			put("cgubao", CameraGubao.class);
			put("dazzle", XuanCai.class);
			put("warm", XuanGuang.class);
			put("normalization", Normalization.class);
			put("autocontrast", AutoContrast.class);
			put("autocolor", AutoColor.class);
			put("equlizehist", HistogramEqulization.class);
			put("whitecolorlevel", WhiteColorLevel.class);
			put("chdr", CameraHdr.class);
			put("cscenery", CameraScenery.class);
			put("cfood", CameraFood.class);
			put("cportrait", CameraPortrait.class);
			put("cdanya", CameraDanya.class);
			put("quhuang", QuHuang.class);
			put("fleeting", JiuShiGuang.class);
			put("ansel", Ansel.class);
			put("country", Country.class);
			put("lakepath", LakePath.class);
			put("lomo10", Lomo10.class);
			put("lomopath", LomoPath.class);
			put("propath", ProPath.class);
			put("louguang", LouGuang.class);
			put("louguang2", LouGuang2.class);
			put("time", Diana.class);
//			put("amaro", Amaro.class);
			put("natamaro", NativeAmaro.class);
			put("rotateclockwise",RotateClockwise.class);
			put("rotateanticlkwise",RotateAntiClockwise.class);
			put("fliphorizontal",FlipHorizontal.class);
			put("flipvertical",FlipVertical.class);
			put("old", Ageing.class);
			put("ageing", Archive.class);
			put("heaven", BanBo.class);
			put("bluetone", BlueTone.class);
			put("art", ClassicHDR.class);
			put("lomo", ClassicLomo.class);
			put("pupple", DarkBlue.class);
			put("romantic", Dreamy.class);
			put("timetravel", Emission.class);
			put("etoc", Etoc.class);
			put("gray", Gray.class);
			put("hdr", HDR.class);
			put("neon", NiHong.class);
			put("pop", Pop.class);
			put("positive", Postive.class);
			put("elegant", QingXin.class);
			put("classichdr", ReallyClassicHDR.class);
			put("singleblue", SingleColorBlue.class);
			put("singlecyan", SingleColorCyan.class);
			put("singlegreen", SingleColorGreen.class);
			put("singlepurple", SingleColorPurple.class);
			put("singlered", SingleColorRed.class);
			put("singleyellow", SingleColorYellow.class);
			put("sketch", Sketch.class);
			put("soft", Soften.class);
			put("spray", Spoondrift.class);
			put("sunny", Sunny.class);
			put("sweet", Sweety.class);
			put("star", XingGuang.class);
			put("snow", XueHua.class);
			put("yellow", Yellow.class);
			put("painting", YouHua.class);
			put("jade", Jade.class);
			
			//新添加测试Filter
			put("yanlitest", CameraYanliTest.class);
			put("decolorization", Decolorization.class);
			put("whitebalance", WhiteBalance.class);
			put("autocontrast", autoContrast_1.class);
			put("localenhance", LocaEnhance.class);
			put("clahergb3", CLAHERGB3.class);
			put("hdrsimple", HDRsimple.class);
			put("posterize", Posterize.class);
			put("kirsch", Kirsch.class);
			put("kirsch1", Kirsch1.class);
			put("skinbeeps", SkinBEEPS.class);
			
			put("lipstick", LipStick.class);
			
			put("intelligentusm", IntelligentUSM.class);
			put("smoothbrightskin", SmoothBrightSkin.class);
			put("shadowhighlight", ShadowHighLight.class);
			put("shadowhighlight1", ShadowHighLight1.class);
			put("shadowhighlight2", ShadowHighLight2.class);
			put("clshadowhighlight", CameraShadowHighLight.class);
			
			// 以下滤镜超级相机使用
			put("original", Original.class);
			//人像
			put("clfugu", CameraFuguLive.class);
			put("clkeren", CameraKerenLive.class);
			put("clvivid", CameraVividLive.class);
			put("cllomo", CameraLomoLive.class);
			put("clrixi", CameraRixiLive.class);
			put("clweimei", CameraWeimeiLive.class);
			put("cmeibai", CameraMeibai.class);//美白
			put("cmenghuan", CameraMenghuan.class);//美幻
			put("cqingxin", CameraQingxin.class);//清新
			put("justsmooth", EffectSmoothSkinAuto.class);
			//风景
			put("clenhance", SceneEnhance.class);
			put("clrise", CameraRiseLive.class);
			put("clhefe", CameraHefeLive.class);
			put("clwalden", CameraWaldenLive.class);
			put("clfuguscenery", CameraFuguSceneryLive.class);
			put("cqiuse", CameraQiuse.class);
			put("cllomoscenery", CameraLomoSceneryLive.class);
			//美食
			put("clfood1", CameraFoodLive1.class);
			put("clfood2", CameraFoodLive2.class);
			put("clfood3", CameraFoodLive3.class);
			put("clfood4", CameraFoodLive4.class);
			put("clfood5", CameraFoodLive5.class);
			put("clfood6", CameraFoodLive6.class);
			//智能
			put("cljiuguan", CameraJiuguanLive.class);//酒馆
			put("cldushi", CameraDushiLive.class);//都市
			put("clguangyin", CameraGuangyinLive.class);//光影
			put("clyazhi", CameraYazhiLive.class);//雅致
			put("clzaoan", CameraZaoanLive.class);
			put("clvividscenery", CameraVividLive.class);
			// 新加
			put("clheibai", CameraHeibaiLive.class);
			put("clcaisefupian", CameraCaiSeFuPianLive.class);//负片
			put("clmidway", CameraMidwayLive.class);
			put("clm3", CameraM3Live.class);
			put("clgoldfinch", CameraGoldFinchLive.class);//
			put("clmeadow", CameraMeadowLive.class);//草地
			put("cllouguang", CameraLouguangLive.class);//漏光
			put("clyanli", CameraYanliLive.class);//艳丽
			//艺术
			put("watercolor", WaterColor.class);
			put("pencil", Pencil.class);
			put("colorpencil", ColorPencil.class);
			put("bluecolor", BlueEdg.class);
			put("oilpainting", OilPainting.class);
			put("hopeeffect", HopeEffect.class);
			
	
			// 以下滤镜copy自魔图
			put("fliphorizontal", FlipHorizontal.class);
			put("flipvertical", FlipVertical.class);
			put("rotateclockwise", RotateClockwise.class);
			put("rotatecounter", RotateCounterClockwise.class);
			put("black_eye", BlackEyeAuto.class);//	
			put("liu_nian", LiuNian.class);//流年
			put("hei_bai", HeiBai.class);//黑白
			put("dianapath", DianaPath.class);//
			put("hui_yi", HuiYi.class);//回忆
			put("lomopath", LomoPath.class);//复古
			put("lou_guang", LouGuang.class);//流光2
			put("louguang2", LouGuang2.class);//流光2
			put("shi_guang", ShiGuang.class);//时光
			put("qiu_se", QiuSe.class);//秋色
			put("lv_ye_xian_zong", LvYeXianZong.class);//绿野仙踪
			put("lan_diao", LanDiao.class);//蓝调
			put("shen_lan", ShenLan.class);//深蓝
			put("meng_huan", MengHuan.class);//梦幻
			put("chuan_yue", ChuanYue.class);//穿越
			put("hdr", HDR.class);//HDR
			put("fu_gu", FuGu.class);//复古
			put("ni_hong", NiHong.class);//霓虹
			put("zheng_pian", ZhengPian.class);//正片
			put("qing_xin", QingXin.class);//清新
			put("xian_huo", XianHuo.class);//鲜活
			put("dan_se_zi", DanSeZi.class);//单色字
			put("su_miao", SuMiao.class);//素描
			put("ri_zhao", RiZhao.class);//日照
			put("tang_shui_pian", TangShuiPian.class);//糖水片
			put("xuan_guang", XuanGuang.class);//炫光
			put("piao_xue", PiaoXue.class);//飘雪
			put("fan_huang", FanHuang.class);//泛黄
			put("lomo", Lomo.class);//复古			
			put("wei_mei", WeiMei.class);//唯美
			put("mei_bai",MeiBai.class);//美白
			put("ke_ren",Keren.class);//可人
			put("fen_nen",FenNen.class);//粉嫩
			put("zi_ran",ZiRan.class);//自然

		}
	};

	private static final Map<String, Class<? extends GlobalFilter>> sGlobalFilters = new HashMap<String, Class<? extends GlobalFilter>>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3871725811199973624L;
		{
			put("viberation", Viberation.class);
			put("lomodrag", LomoDrag.class);
			put("testskin", TestSkin.class);
			
			// For Test
			put("colortemperature",ColorTemperature.class);
			
			
			
			put("smoothskin", SmoothSkin.class);
			put("betterskin", BetterSkin.class);
			put("brightness", Brightness.class);
			put("contrast", Contrast.class);
			put("saturation", Saturation.class);
			put("sharpen", Sharpen.class);
			put("eyeenlargeauto",EyeEnlargeAuto.class);
		}
	};

	private static final Map<String, Class<? extends PartialFilter>> sPartialFilters = new HashMap<String, Class<? extends PartialFilter>>() {

		private static final long serialVersionUID = 3137979707634276137L;

		/**
		 * 
		 */
		{
			put("whelkremove2", WhelkRemoveTest2.class);
			
			put("thin", Thin.class);
			put("eyeenlarge", EyeEnlarge.class);
			put("whelkremove", WhelkRemove.class);
			put("crop", Crop.class);
			put("blackeyeremove", BlackEyeRemove.class);
			put("redeyeremove", RedEyeRemove.class);
			put("backgroundblurround", BackGroundBlurRound.class);
			put("backgroundblurline", BackGroundBlurLine.class);
			
		}
	};

	private static final Map<String, Class<? extends RealsizeFilter>> sRealsizeFilters = new HashMap<String, Class<? extends RealsizeFilter>>() {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1066164366972013669L;

		{
			put("rsoriginal",RSEmptyFilter.class);
			put("rsrotate",RSRotate.class);
			put("rscounter",RSCounterRotate.class);
			put("rsansel", RSAnsel.class);
			put("rsfliphorizontal", RSFlipHorizontal.class);
			put("rscllomo",RSCameraLomoLive.class);
			put("rscliunian",RSCameraLiunian.class);
			put("rscxuancai", RSCameraXuancai.class);
			put("rscdiana",RSCameraDiana.class);
			put("rsclfugu",RSCameraFuguLive.class);
			put("rschuiyi",RSCameraHuiyi.class);
			put("rsclg3", RSCameraG3Live.class);
			put("rsclsutro", RSCameraSutroLive.class);
			put("rsclvivid", RSCameraVividLive.class);
			
			//风景
			put("rsclrise", RSCameraRiseLive.class);
			put("rsclwalden", RSCameraWaldenLive.class);
			put("rsclhefe", RSCameraHefeLive.class);
			put("rsclfuguscenery",RSCameraFuguSceneryLive.class);
			put("rscllomoscenery",RSCameraLomoSceneryLive.class);
			put("rscqiuse", RSCameraQiuse.class);
			put("rsclenhance", RSCameraSceneEnhance.class);
			put("rsclm3", RSCameraM3Live.class);
			put("rsclgoldfinch", RSCameraGoldFinchLive.class);
			put("rsclmeadow", RSCameraMeadowLive.class);

			//美食
			put("rsclfood1", RSCameraFoodLive1.class);
			put("rsclfood2", RSCameraFoodLive2.class);
			put("rsclfood3", RSCameraFoodLive3.class);
			put("rsclfood4", RSCameraFoodLive4.class);
			put("rsclfood5", RSCameraFoodLive5.class);
			put("rsclfood6", RSCameraFoodLive6.class);
			
			//智能
			put("rscljiuguan", RSCameraJiuguanLive.class);
			put("rscldushi", RSCameraDushiLive.class);
			put("rsclguangyin", RSCameraGuangyinLive.class);
			put("rsclyazhi", RSCameraYazhiLive.class);
			put("rsclzaoan", RSCameraZaoanLive.class);
			put("rsclvividscenery", RSCameraVividLive.class);
			put("rsclheibai", RSCameraHeibaiLive.class);
			put("rsclcaisefupian", RSCameraCaiSeFuPianLive.class);
			put("rsclmidway", RSCameraMidwayLive.class);
			
			// 新增
			put("rsclyanli", RSCameraYanliLive.class);
			put("rscllouguang", RSCameraLouguangLive.class);
			
			// For Test
			put("rsprogressive", RSTestYBW.class);
			put("rsdecolorization", RSDecolorization.class);
		}
	};

	public static OneKeyFilter createOneKeyFilter(Context cx, String label) {
		PackageSecurity.check(cx);
		if(!TextUtils.isEmpty(label) && !TextUtils.isEmpty(label.toLowerCase())){
			label = label.toLowerCase();
			try {
				return sOneKeyFilters.get(label).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				String msg = GET_FILTER_ERROR_MSG + " OneKey Filter Error: your label is : " + label;
				//throw new RuntimeException(msg);
				ErrorHandleHelper.handleErrorMsg(msg, GET_FILTER_TAG);
			}
		}
		
		try {
			return sOneKeyFilters.get("original").newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static GlobalFilter createGlobalFilter(Context cx, String label) {
		PackageSecurity.check(cx);
		label = label.toLowerCase();
		try {
			return sGlobalFilters.get(label).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			String msg = GET_FILTER_ERROR_MSG + "Global Filter Error: your label is : " + label;
			ErrorHandleHelper.handleErrorMsg(msg, GET_FILTER_TAG);
		}
		return null;
	}

	public static PartialFilter createPartialFilter(Context cx, String label) {
		PackageSecurity.check(cx);
		label = label.toLowerCase();
		try {
			return sPartialFilters.get(label).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			String msg = GET_FILTER_ERROR_MSG + "Partial Filter Error: your label is : " + label;
			ErrorHandleHelper.handleErrorMsg(msg, GET_FILTER_TAG);
		}
		return null;
	}
	
	public static RealsizeFilter createRealsizeFilter(Context cx, String label) {
		PackageSecurity.check(cx);
		label = label.toLowerCase();
		try {
			return sRealsizeFilters.get(label).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			String msg = GET_FILTER_ERROR_MSG + "Realsize Filter Error: your label is : " + label;
			ErrorHandleHelper.handleErrorMsg(msg, GET_FILTER_TAG);
		}
		
		try {
			return sRealsizeFilters.get("rsoriginal").newInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
