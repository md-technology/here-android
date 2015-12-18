package com.mdtech.social.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "user_settings")
public class UserSettings {

	@JsonProperty("user_id")
    private Long userId;
	
	@JsonProperty("user_avatar")
	private Long userAvatar;
	
	// 昵称
	@JsonProperty("name")
	private String name;
	
	// 网址名称，它将会出现在您的网址中
	@JsonProperty("url_name")
	private String urlName;
	
	// 您的网页
	@JsonProperty("homepage_url")
	private String homepageUrl;
	
	// 写一些关于您自己
	@JsonProperty("description")
	private String description;
	
	// 我的照片有新的评论
	@JsonProperty("alert_comments")
	private Boolean alertComments;
	
	// 我订阅的用户上传了新照片
	@JsonProperty("alert_photos")
	private Boolean alertPhotos;
	
	// 新的和更新后的群组加入邀请
	@JsonProperty("alert_group_invitations")
	private Boolean alertGroupInvitations;
	
	// 启用私密信息
	@JsonProperty("private_messages")
	private Boolean privateMessages;
	
	// 保留所有权利
	@JsonProperty("all_rights_reserved")
	private Boolean allRightsReserved;
	
	// 是否允许用作商业用途？
	@JsonProperty("commercial_use")
	private Boolean commercialUse;
	
	// 是否允许修改？
	@JsonProperty("modify")
	private Boolean modify;
	
	// 已使用存储空间大小
	@JsonProperty("storage_space")
	private Double storageSpace;
	
	@JsonProperty("auto_upload")
	private boolean autoUpload;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrlName() {
		return urlName;
	}

	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}

	public String getHomepageUrl() {
		return homepageUrl;
	}

	public void setHomepageUrl(String homepageUrl) {
		this.homepageUrl = homepageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getAlertComments() {
		return alertComments;
	}

	public void setAlertComments(Boolean alertComments) {
		this.alertComments = alertComments;
	}

	public Boolean getAlertPhotos() {
		return alertPhotos;
	}

	public void setAlertPhotos(Boolean alertPhotos) {
		this.alertPhotos = alertPhotos;
	}

	public Boolean getAlertGroupInvitations() {
		return alertGroupInvitations;
	}

	public void setAlertGroupInvitations(Boolean alertGroupInvitations) {
		this.alertGroupInvitations = alertGroupInvitations;
	}

	public Boolean getPrivateMessages() {
		return privateMessages;
	}

	public void setPrivateMessages(Boolean privateMessages) {
		this.privateMessages = privateMessages;
	}

	public Boolean getAllRightsReserved() {
		return allRightsReserved;
	}

	public void setAllRightsReserved(Boolean allRightsReserved) {
		this.allRightsReserved = allRightsReserved;
	}

	public Boolean getCommercialUse() {
		return commercialUse;
	}

	public void setCommercialUse(Boolean commercialUse) {
		this.commercialUse = commercialUse;
	}

	public Boolean getModify() {
		return modify;
	}

	public void setModify(Boolean modify) {
		this.modify = modify;
	}

	public Long getUserAvatar() {
		return userAvatar;
	}

	public void setUserAvatar(Long userAvatar) {
		this.userAvatar = userAvatar;
	}

	public Double getStorageSpace() {
		return storageSpace;
	}

	public void setStorageSpace(Double storageSpace) {
		this.storageSpace = storageSpace;
	}

	public boolean isAutoUpload() {
		return autoUpload;
	}

	public void setAutoUpload(boolean autoUpload) {
		this.autoUpload = autoUpload;
	}	
	
}
