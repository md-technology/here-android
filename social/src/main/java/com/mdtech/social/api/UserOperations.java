package com.mdtech.social.api;

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
    PonmapProfile getUserProfile();

    /**
     *
     * @param userId
     * @return
     */
    PonmapProfile getUserProfile(String userId);
}
