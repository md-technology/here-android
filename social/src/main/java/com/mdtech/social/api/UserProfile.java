package com.mdtech.social.api;

import java.io.Serializable;

/**
 * Created by any on 2014/10/30.
 */
public class UserProfile implements Serializable {

    private final String id;

    private final String username;

    private final String name;

    private String image;

    private String coverImage;

    private String website;

    private String email;

    private boolean login;

    public UserProfile(String id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
