package com.mdtech.social.api.impl;

import com.mdtech.social.connect.HereServiceProvider;

import org.springframework.social.MissingAuthorizationException;
import org.springframework.social.support.URIBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * Created by any on 2014/10/30.
 */
public class AbstractPonmapOperations {

    static final String BASE_API_URL = HereServiceProvider.BASE_URL + "/api/rest";

    private final boolean isAuthorized;

    private static final LinkedMultiValueMap<String, String> EMPTY_PARAMETERS = new LinkedMultiValueMap<String, String>();


    public AbstractPonmapOperations(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    protected void requireAuthorization() {
        if (!isAuthorized) {
            throw new MissingAuthorizationException(HereServiceProvider.PROVIDER_ID);
        }
    }

    protected URI buildUri(String path) {
        return buildUri(path, EMPTY_PARAMETERS);
    }

    protected URI buildUri(String path, String parameterName,
                           Object parameterValue) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.set(parameterName, parameterValue.toString());
        return buildUri(path, parameters);
    }

    protected URI buildUri(String path, MultiValueMap<String, String> parameters) {
        return URIBuilder.fromUri(BASE_API_URL + path).queryParams(parameters)
                .build();
    }
}
