package com.mdtech.social.api.impl;

import com.mdtech.social.api.UserProfile;
import com.mdtech.social.api.UserOperations;
import com.mdtech.social.api.model.*;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiven.wang on 2014/10/30.
 */
public class UserTemplate extends AbstractHereOperations implements UserOperations {

    private static final String PATH = "/user";

    private final RestTemplate restTemplate;

    public UserTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }

    @Override
    public org.springframework.social.connect.UserProfile getUserProfile() {
//        requireAuthorization();
//        User user = restTemplate.getForObject(BASE_API_URL + PATH , User.class);
//
//        if(null != user) {
//            UserProfile profile = new UserProfile(user.getId().toString(), user.getUsername(), user.getName());
//            if(user.getAvatar() != null) {
//                profile.setImage(user.getAvatar().getOssKey());
//            }
//            if(null != user.getProfileCover()) {
//                profile.setCoverImage(user.getProfileCover().getOssKey());
//            }
//            profile.setEmail(user.getEmail());
//            profile.setWebsite(user.getWebsite());
//            profile.setLogin(true);
//            return profile;
//        }
        return null;
    }

    @Override
    public org.springframework.social.connect.UserProfile getUserProfile(BigInteger id) {
//
//        User user = get(id);
//
//        org.springframework.social.connect.UserProfile profile =
//                new org.springframework.social.connect.UserProfile(user.getName(), "", "", user.getEmail(), user.getUsername());

        return null;
    }

    @Override
    public UserProfile getHereProfile() {
        requireAuthorization();
        User user = restTemplate.getForObject(BASE_API_URL + PATH , User.class);

        if(null != user) {
            UserProfile profile = new UserProfile(user.getId().toString(), user.getUsername(), user.getName());
            if(user.getAvatar() != null) {
                profile.setImage(user.getAvatar().getOssKey());
            }
            if(null != user.getProfileCover()) {
                profile.setCoverImage(user.getProfileCover().getOssKey());
            }
            profile.setEmail(user.getEmail());
            profile.setWebsite(user.getWebsite());
            profile.setLogin(true);
            return profile;
        }
        return null;
    }

    @Override
    public User get(BigInteger id) {
        User user = restTemplate.getForObject(BASE_API_URL + PATH + "/" + id, User.class);
        // TODO
        if(user.getMembers() != null && user.getMembers().size() > 0) {
            user.setRole("group");
        }else {
            user.setRole("user");
        }
        return user;
    }

    @Override
    public List<Photo> getPhotos(BigInteger id, Integer pageSize, Integer pageNo) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("pageSize", pageSize.toString());
        parameters.add("pageNo", pageNo.toString());
        return restTemplate.getForObject(
                buildUri(PATH + "/" + id + "/photos", parameters), com.mdtech.social.api.model.PhotoList.class);
    }

    @Override
    public List<Album> getAlbums(BigInteger id, Integer pageSize, Integer pageNo) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("pageSize", pageSize.toString());
        parameters.add("pageNo", pageNo.toString());
        return restTemplate.getForObject(
                buildUri(PATH + "/" + id + "/albums", parameters), AlbumList.class);
    }

    @Override
    public List<User> getGroups(BigInteger id, Integer pageSize, Integer pageNo) {
        return restTemplate.getForObject(
                buildUri(PATH + "/" + id + "/groups", getPagableParams(pageSize, pageNo)), UserList.class);
    }

    @Override
    public List<User> getMembers(BigInteger id, Integer pageSize, Integer pageNo) {
        // TODO no implementation in server
        User user = get(id);
        int fromIndex = pageNo*pageSize;
        int toIndex = (pageNo+1)*pageSize;
        if(null == user.getMembers() || fromIndex+1 > user.getMembers().size()) {
            return new ArrayList<User>();
        }
        if(toIndex>user.getMembers().size()) {
            toIndex = user.getMembers().size();
        }
        return user.getMembers().subList(fromIndex, toIndex);
    }

}
