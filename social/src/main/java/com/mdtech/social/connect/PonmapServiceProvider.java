package com.mdtech.social.connect;

import com.mdtech.social.api.Ponmap;
import com.mdtech.social.api.impl.PonmapTemplate;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

/**
 * Created by any on 2014/10/29.
 */
public class PonmapServiceProvider extends AbstractOAuth2ServiceProvider<Ponmap> {

    public static final String BASE_URL = "http://www.photoshows.cn";
    public static final String PROVIDER_ID = "ponmap";


    /**
     * Create a new {@link PonmapServiceProvider}.
     *
     * @param clientId
     * @param clientSecret
     */
    public PonmapServiceProvider(String clientId, String clientSecret) {
        super(new OAuth2Template(clientId, clientSecret, BASE_URL+"/oauth/authorize", BASE_URL+"/oauth/token"));
        ((OAuth2Template)getOAuthOperations()).setUseParametersForClientAuthentication(true);
    }

    @Override
    public Ponmap getApi(String accessToken) {
        return new PonmapTemplate(accessToken);
    }
}
