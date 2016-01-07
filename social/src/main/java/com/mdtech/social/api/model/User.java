package com.mdtech.social.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by any on 2014/10/24.
 */
public class User implements Serializable {

    private String role;

    private BigInteger id;

    // 系统用户名
    private String username;

    // 昵称
    private String name;

    // 头像
    private Cover avatar;

    // 描述
    private String description;

    //email
    private String email;

    //website
    private String website;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;

    // 用户card背景
    private Cover profileCover;
    private Cover mastheadCover;

    // 其所有标签
    private Set<String> tags = new HashSet<String>(0);

    @JsonProperty("follow")
    private Boolean follow;

    private User user;
    private UserList members;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Cover getAvatar() {
        return avatar;
    }

    public void setAvatar(Cover avatar) {
        this.avatar = avatar;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getFollow() {
        return follow;
    }

    public void setFollow(Boolean follow) {
        this.follow = follow;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Cover getProfileCover() {
        return profileCover;
    }

    public void setProfileCover(Cover profileCover) {
        this.profileCover = profileCover;
    }

    public Cover getMastheadCover() {
        return mastheadCover;
    }

    public void setMastheadCover(Cover mastheadCover) {
        this.mastheadCover = mastheadCover;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public UserList getMembers() {
        return members;
    }

    public void setMembers(UserList members) {
        this.members = members;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
