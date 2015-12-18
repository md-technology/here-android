package com.mdtech.social.api.model;

/**
 * Created by any on 2014/9/28.
 */
public class AbstractResponse {

    public static enum Status {
        /**
         * 一切正常
         */
        OK,
        /**
         * 图片ID格式错误
         */
        ID_FORMAT_ERROR,
        /**
         * 输入参数错误
         */
        PARAM_ERROR,
        /**
         * 找不到对象
         */
        NO_ENTITY,
        /**
         * 未授权
         */
        NO_AUTHORIZE,
        /**
         * 访问被拒绝
         */
        ACCESS_DENIED,
        /**
         * 出现异常
         */
        EXCEPTION
    }

    private Status status;

    private String info;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
