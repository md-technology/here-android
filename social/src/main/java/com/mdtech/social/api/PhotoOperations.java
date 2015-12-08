package com.mdtech.social.api;

import com.mdtech.social.api.json.Photo;

public interface PhotoOperations {

    /**
     * 上传图片
     *
     * @param lat
     * @param lng
     * @param address
     * @param vendor
     * @param file
     * @return
     */
    Photo upload(String lat,
                        String lng,
                        String address,
                        String vendor,
                        String file);

    /**
     * 获取图片常用属性
     *
     * @param id
     * @return
     */
    Photo get(String id);
}
