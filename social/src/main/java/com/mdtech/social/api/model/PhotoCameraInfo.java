package com.mdtech.social.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Created by tiwen.wang on 11/18/2014.
 */
public class PhotoCameraInfo {

    private Long id;

    @JsonProperty("create_date")
    private Date createDate;
    private Double lat;
    private Double lng;
    private Double alt;
    @JsonProperty("username")
    private String userName;

    // 相机型号
    private String model;
    // 相机品牌
    private String make;

    // 拍摄日期
    @JsonProperty("date_time_original")
    private Date dateTimeOriginal;
    // 曝光时间
    @JsonProperty("exposure_time")
    private Double exposureTime;
    // 焦距
    @JsonProperty("focal_length")
    private String focalLength;
    // 光圈
    @JsonProperty("fnumber")
    private String FNumber;
    // ISO
    private String ISO;
    // 曝光补偿
    @JsonProperty("exposure_bias")
    private String exposureBias;
    // 闪光灯
    private Short Flash;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getAlt() {
        return alt;
    }

    public void setAlt(Double alt) {
        this.alt = alt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public Date getDateTimeOriginal() {
        return dateTimeOriginal;
    }

    public void setDateTimeOriginal(Date dateTimeOriginal) {
        this.dateTimeOriginal = dateTimeOriginal;
    }

    public Double getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(Double exposureTime) {
        this.exposureTime = exposureTime;
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focalLength) {
        this.focalLength = focalLength;
    }

    public String getFNumber() {
        return FNumber;
    }

    public void setFNumber(String FNumber) {
        this.FNumber = FNumber;
    }

    public String getISO() {
        return ISO;
    }

    public void setISO(String ISO) {
        this.ISO = ISO;
    }

    public String getExposureBias() {
        return exposureBias;
    }

    public void setExposureBias(String exposureBias) {
        this.exposureBias = exposureBias;
    }

    public Short getFlash() {
        return Flash;
    }

    public void setFlash(Short flash) {
        Flash = flash;
    }
}
