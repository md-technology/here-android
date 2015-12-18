package com.mdtech.social.api;

import com.mdtech.social.api.model.Photo;
import com.mdtech.social.api.model.User;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by any on 2014/10/30.
 */
public interface UserOperations {

    /**
     * Retrieves the profile for the authenticated user.
     * @return the user's profile information.
     * @throws org.springframework.social.ApiException if there is an error while communicating with Facebook.
     * @throws org.springframework.social.MissingAuthorizationException if FacebookTemplate was not created with an access token.
     */
    UserProfile getUserProfile();

    /**
     *
     * @param id
     * @return
     */
    UserProfile getUserProfile(BigInteger id);

    /**
     *
     * @param id
     * @return
     */
    User get(BigInteger id);

    /**
     *
     * @param id
     * @param pageSize
     * @param pageNo
     * @return
     */
    List<Photo> getPhotos(BigInteger id, Integer pageSize, Integer pageNo);
}
