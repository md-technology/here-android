package com.mdtech.social.api;

import com.mdtech.social.api.model.Image;
import com.mdtech.social.api.model.Location;
import com.mdtech.social.api.model.Photo;

import org.springframework.core.io.Resource;

import java.util.Set;

public interface PhotoOperations {

    /**
     * 上传图片
     * @param image
     * @param location
     * @param tags
     * @param album
     * @param is360
     * @param file
     * @return
     */
    Photo upload(Image image, Location location, Set<String> tags, String album, boolean is360, Resource file);

    /**
     * 获取图片常用属性
     *
     * @param id
     * @return
     */
    Photo get(String id);
}
