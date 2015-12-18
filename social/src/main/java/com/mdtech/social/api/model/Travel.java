package com.mdtech.social.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by any on 2014/10/24.
 */
public class Travel {

    private Long id;
    @JsonProperty("user_id")
    private Long userId;
    private String username;

    private com.mdtech.social.api.model.User user;

    private List<TravelSpot> spots = new ArrayList<TravelSpot>(0);
    private TravelSpot spot;
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

    private List<com.mdtech.social.api.model.Photo> photos = new ArrayList<com.mdtech.social.api.model.Photo>(0);

    @JsonProperty("like_count")
    private Integer likeCount;

    // 相册封面图片
    @JsonProperty("album_cover")
    private com.mdtech.social.api.model.Photo albumCover;

    // 图片数量
    @JsonProperty("photo_size")
    private Integer photoSize;

    // 所属消息
    @JsonProperty("message_id")
    private Long messageId;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public List<TravelSpot> getSpots() {
        return spots;
    }
    public void setSpots(List<TravelSpot> spots) {
        this.spots = spots;
    }
    public TravelSpot getSpot() {
        return spot;
    }
    public void setSpot(TravelSpot spot) {
        this.spot = spot;
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
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public Integer getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
    public com.mdtech.social.api.model.Photo getAlbumCover() {
        return albumCover;
    }
    public void setAlbumCover(com.mdtech.social.api.model.Photo albumCover) {
        this.albumCover = albumCover;
    }
    public com.mdtech.social.api.model.User getUser() {
        return user;
    }
    public void setUser(com.mdtech.social.api.model.User user) {
        this.user = user;
    }
    public Integer getPhotoSize() {
        return photoSize;
    }
    public void setPhotoSize(Integer photoSize) {
        this.photoSize = photoSize;
    }
    public Long getMessageId() {
        return messageId;
    }
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public List<com.mdtech.social.api.model.Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<com.mdtech.social.api.model.Photo> photos) {
        this.photos = photos;
    }
}
