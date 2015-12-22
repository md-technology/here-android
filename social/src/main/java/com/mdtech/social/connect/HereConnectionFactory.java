package com.mdtech.social.connect;

import com.mdtech.social.api.HereApi;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

/**
 * Created by any on 2014/10/29.
 */
public class HereConnectionFactory extends OAuth2ConnectionFactory<HereApi> {

    /**
     * Create a {@link org.springframework.social.connect.support.OAuth2ConnectionFactory}.
     *
     * @param clientId
     * @param clientSecret
     */
    public HereConnectionFactory(String clientId, String clientSecret) {
        super(HereServiceProvider.PROVIDER_ID, new HereServiceProvider(clientId, clientSecret), new HereAdapter());
    }
}
