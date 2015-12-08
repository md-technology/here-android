package com.mdtech.social.api.impl;

import com.mdtech.social.connect.PonmapServiceProvider;

import org.springframework.social.MissingAuthorizationException;

/**
 * Created by any on 2014/10/30.
 */
public class AbstractPonmapOperations {

    static final String BASE_API_URL = PonmapServiceProvider.BASE_URL + "/api/rest";

    private final boolean isAuthorized;

    public AbstractPonmapOperations(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    protected void requireAuthorization() {
        if (!isAuthorized) {
            throw new MissingAuthorizationException(PonmapServiceProvider.PROVIDER_ID);
        }
    }
}
