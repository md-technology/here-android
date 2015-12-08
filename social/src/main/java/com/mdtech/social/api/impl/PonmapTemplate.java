package com.mdtech.social.api.impl;

import com.mdtech.social.api.PanoramioOperations;
import com.mdtech.social.api.PhotoOperations;
import com.mdtech.social.api.Ponmap;
import com.mdtech.social.api.TravelOperations;
import com.mdtech.social.api.UserOperations;

import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.ClientHttpRequestFactorySelector;

/**
 * Created by any on 2014/10/29.
 */
public class PonmapTemplate extends AbstractOAuth2ApiBinding implements Ponmap {

    private UserOperations userOperations;
    private TravelOperations travelOperations;
    private PanoramioOperations panoramioOperations;
    private PhotoOperations photoOperations;

    public PonmapTemplate(String accessToken) {
        super(accessToken);
        initialize();
    }

    @Override
    public TravelOperations travelOperations() {
        return travelOperations;
    }

    @Override
    public UserOperations userOperations() {
        return userOperations;
    }

    @Override
    public PanoramioOperations panoramioOperations() {
        return panoramioOperations;
    }

    @Override
    public PhotoOperations photoOperations() {
        return photoOperations;
    }

    // private helpers
    private void initialize() {
        // Wrap the request factory with a BufferingClientHttpRequestFactory so that the error handler can do repeat reads on the response.getBody()
        super.setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(getRestTemplate().getRequestFactory()));
        initSubApis();
    }

    private void initSubApis() {
        userOperations = new UserTemplate(getRestTemplate(), isAuthorized());
        travelOperations = new TravelTemplate(getRestTemplate(), isAuthorized());
        panoramioOperations = new PanoramioTemplate(getRestTemplate(), isAuthorized());
        photoOperations = new PhotoTemplate(getRestTemplate(), isAuthorized());
    }
}
