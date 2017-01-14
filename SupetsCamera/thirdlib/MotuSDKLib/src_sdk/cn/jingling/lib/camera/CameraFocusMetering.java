package cn.jingling.lib.camera;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.Parameters;
import android.os.Build;

import cn.jingling.lib.PackageSecurity;

/**
 * 独立测光功能底层接口主要实现类
 *
 * @author zhaozheng01
 */

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class CameraFocusMetering {

    private List<Area> mFocusList, mMeteringList;

    /**
     * 初始化
     */
    public CameraFocusMetering(Context cx) {
        PackageSecurity.check(cx);
        mFocusList = new ArrayList<Area>();
        mMeteringList = new ArrayList<Area>();
        mFocusList.add(new Area(new Rect(), 1));
        mMeteringList.add(new Area(new Rect(), 1));
    }

    /**
     * 在改变测光点后调用
     *
     * @param camera Camera类的实例
     * @param area   测光区域，以相机坐标提供
     */
    public void changeMeteringArea(Camera camera, Rect area) {
        Parameters params = camera.getParameters();
        mMeteringList.get(0).rect.set(area);
        params.setMeteringAreas(mMeteringList);
        camera.setParameters(params);
    }

    /**
     * 改变对焦点后调用
     *
     * @param camera Camera类实例
     * @param area   对焦区域，以相机坐标提供
     */
    public void changeFocusArea(Camera camera, Rect area) {
        Parameters params = camera.getParameters();
        mFocusList.get(0).rect.set(area);
        params.setFocusAreas(mFocusList);
        camera.setParameters(params);
    }

    /**
     * 锁定和解锁测光
     *
     * @param camera Camera类实例
     * @param lock   是否锁定测光值
     */
    public void lockMetering(Camera camera, boolean lock) {
        Parameters params = camera.getParameters();
        params.setAutoExposureLock(lock);
        camera.setParameters(params);
    }

    /**
     * 获得测光锁状态
     *
     * @param camera Camera类实例
     * @return 测光锁是否锁定
     */
    public boolean getMeteringLock(Camera camera) {
        Parameters params = camera.getParameters();
        return params.getAutoExposureLock();
    }

}
