package com.mdtech.social.connect;

import com.mdtech.social.api.HereApi;

import org.springframework.social.connect.support.OAuth2ConnectionFactory;

/**
 * Created by any on 2014/10/29.
 */
public class PonmapConnectionFactory extends OAuth2ConnectionFactory<HereApi> {

    /**
     * Create a {@link org.springframework.social.connect.support.OAuth2ConnectionFactory}.
     *
     * @param clientId
     * @param clientSecret
     */
    public PonmapConnectionFactory(String clientId, String clientSecret) {
        super(PonmapServiceProvider.PROVIDER_ID, new PonmapServiceProvider(clientId, clientSecret), new PonmapAdapter());
    }
}
