package com.mdtech.social.api;

import org.springframework.social.ApiBinding;

/**
 * Created by any on 2014/10/29.
 */
public interface Ponmap extends ApiBinding {

    /**
     * 专辑
     *
     * @return
     */
    AlbumOperations albumOperations();

    /**
     * 用户账户
     *
     * @return
     */
    UserOperations userOperations();

    /**
     * 地图图片索引
     *
     * @return
     */
    PanoramioOperations panoramioOperations();

    /**
     * 图片接口
     *
     * @return
     */
    PhotoOperations photoOperations();

}
