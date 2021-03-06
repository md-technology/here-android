package com.mdtech.social.api;

import org.springframework.social.ApiBinding;

import java.io.Serializable;

/**
 * Created by Tiven.wang on 2014/10/29.
 */
public interface HereApi extends ApiBinding, Serializable {

    /**
     * 注册用户
     * @return
     */
    SignupOperations signupOperations();

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

    /**
     * 评论接口
     * @return
     */
    CommentOperations commentOperations();

    /**
     * 加星接口
     * @return
     */
    LikeOperations likeOperation();

    /**
     * 轨迹接口
     * @return
     */
    TrackOperations trackOperations();
}
