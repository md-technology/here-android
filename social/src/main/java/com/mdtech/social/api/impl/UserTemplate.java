package com.mdtech.social.api.impl;

import com.mdtech.social.api.PonmapProfile;
import com.mdtech.social.api.UserOperations;
import com.mdtech.social.api.json.User;
import com.mdtech.social.api.json.UserResponse;

import org.springframework.web.client.RestTemplate;

/**
 * Created by any on 2014/10/30.
 */
public class UserTemplate extends AbstractPonmapOperations implements UserOperations {

    private final RestTemplate restTemplate;

    public UserTemplate(RestTemplate restTemplate, boolean isAuthorizedForUser) {
        super(isAuthorizedForUser);
        this.restTemplate = restTemplate;
    }

    @Override
    public PonmapProfile getUserProfile() {
        requireAuthorization();
        User user = restTemplate.getForObject(BASE_API_URL + "/user" , User.class);

        if(null != user) {
            PonmapProfile profile = new PonmapProfile(user.getId().toString(), user.getUsername(), user.getName());
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
    public PonmapProfile getUserProfile(String userId) {

        User user = restTemplate.getForObject(BASE_API_URL + "/user/"+userId, User.class);

        PonmapProfile profile = new PonmapProfile(user.getId().toString(), user.getUsername(), user.getName());

        return profile;
    }

}
