package com.mdtech.social.api.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by any on 2014/10/24.
 */
public class TravelSpot {

    private Long id;
    @JsonProperty("travel_id")
    private Long travelId;
    private List<Photo> photos = new ArrayList<Photo>(0);
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("modify_time")
    private Date modifyTime;
    @JsonProperty("time_start")
    private Date timeStart;
    @JsonProperty("time_end")
    private Date timeEnd;
    private String address;
    private String title;
    private String description;
    @JsonProperty("center_lat")
    private Double centerLat;
    @JsonProperty("center_lng")
    private Double centerLng;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getTravelId() {
        return travelId;
    }
    public void setTravelId(Long travelId) {
        this.travelId = travelId;
    }
    public List<Photo> getPhotos() {
        return photos;
    }
    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
    public Date getTimeStart() {
        return timeStart;
    }
    public void setTimeStart(Date timeStart) {
        this.timeStart = timeStart;
    }
    public Date getTimeEnd() {
        return timeEnd;
    }
    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Double getCenterLat() {
        return centerLat;
    }
    public void setCenterLat(Double centerLat) {
        this.centerLat = centerLat;
    }
    public Double getCenterLng() {
        return centerLng;
    }
    public void setCenterLng(Double centerLng) {
        this.centerLng = centerLng;
    }

}
