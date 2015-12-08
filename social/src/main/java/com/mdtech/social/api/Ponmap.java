package com.mdtech.social.api;

import org.springframework.social.ApiBinding;

/**
 * Created by any on 2014/10/29.
 */
public interface Ponmap extends ApiBinding {

    /**
     * 旅行相册
     *
     * @return
     */
    TravelOperations travelOperations();

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
