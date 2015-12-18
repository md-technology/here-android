package com.mdtech.social.connect;

import com.mdtech.social.api.HereApi;

import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

/**
 * Created by any on 2014/10/30.
 */
public class PonmapAdapter implements ApiAdapter<HereApi> {

    @Override
    public boolean test(HereApi api) {
        try {
            api.userOperations().getUserProfile();
            return true;
        }catch (ApiException e) {
            return false;
        }
    }

    @Override
    public void setConnectionValues(HereApi api, ConnectionValues values) {

    }

    @Override
    public UserProfile fetchUserProfile(HereApi api) {
        return null;
    }

    @Override
    public void updateStatus(HereApi api, String message) {

    }
}
