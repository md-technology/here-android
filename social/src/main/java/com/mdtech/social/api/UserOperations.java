package com.mdtech.social.api;

import com.mdtech.social.api.model.Album;
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
    org.springframework.social.connect.UserProfile getUserProfile();

    /**
     *
     * @param id
     * @return
     */
    org.springframework.social.connect.UserProfile getUserProfile(BigInteger id);

    /**
     *
     * @return
     */
    UserProfile getHereProfile();

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


    /**
     *
     * @param id
     * @param pageSize
     * @param pageNo
     * @return
     */
    List<Album> getAlbums(BigInteger id, Integer pageSize, Integer pageNo);

    /**
     *
     * @param id
     * @param pageSize
     * @param pageNo
     * @return
     */
    List<User> getGroups(BigInteger id, Integer pageSize, Integer pageNo);
}
