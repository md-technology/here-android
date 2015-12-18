package com.mdtech.here.panoramio;

import com.mdtech.social.api.PanoramioOperations;
import com.mdtech.social.api.model.Photo;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * Created by any on 2014/10/31.
 */
public interface PanoramioLayer {

    public static final String MARKER_DATA = "photo";

    /**
     * 开启
     */
    public void start();

    /**
     * 停止
     */
    public void stop();

    /**
     * 暂停
     */
    public void pause();

    /**
     * 根据接口取数结果在地图上绘制图片图标
     *
     * @param photos
     */
    public void displayPanoramioPhoto(List<Photo> photos);

    /**
     * 创建图片标记
     *
     * @param photo
     */
    public Target createMarker(Photo photo);

    /**
     * 清理地图上所有覆盖物
     *
     */
    public void clearMap();


    public void setPanoramioOperations(PanoramioOperations panoramioOperations);

    public PanoramioOperations getPanoramioOperations();

    public void trigger();
}
