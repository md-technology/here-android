package com.mdtech.social.connect;

import com.mdtech.social.api.Ponmap;

import org.springframework.social.ApiException;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

/**
 * Created by any on 2014/10/30.
 */
public class PonmapAdapter implements ApiAdapter<Ponmap> {

    @Override
    public boolean test(Ponmap api) {
        try {
            api.userOperations().getUserProfile();
            return true;
        }catch (ApiException e) {
            return false;
        }
    }

    @Override
    public void setConnectionValues(Ponmap api, ConnectionValues values) {

    }

    @Override
    public UserProfile fetchUserProfile(Ponmap api) {
        return null;
    }

    @Override
    public void updateStatus(Ponmap api, String message) {

    }
}
