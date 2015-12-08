package com.mdtech.social.api;

import com.mdtech.social.api.json.Photo;

import java.util.List;

/**
 * Created by any on 2014/10/31.
 */
public interface PanoramioOperations {

    /**
     * 获取地图图片索引
     *
     * @param swLat
     * @param swLng
     * @param neLat
     * @param neLng
     * @param level
     * @return
     */
    public List<Photo> getPanoramio(String swLat,
                                      String swLng,
                                      String neLat,
                                      String neLng,
                                      String level);

    public List<Photo> getPanoramio(String... params);

    /**
     * 在地图图片索引中搜索
     *
     * @param swLat
     * @param swLng
     * @param neLat
     * @param neLng
     * @param level
     * @param width
     * @param height
     * @param term
     * @param type
     * @return
     */
    public List<Photo> search(String swLat,
                                    String swLng,
                                    String neLat,
                                    String neLng,
                                    String level,
                                    String width,
                                    String height,
                                    String term,
                                    String type);
}
