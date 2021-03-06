package com.mdtech.social.connect;

import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.impl.HereApiTemplate;

import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Template;

/**
 * Created by any on 2014/10/29.
 */
public class HereServiceProvider extends AbstractOAuth2ServiceProvider<HereApi> {

    public static final String BASE_URL = "http://www.photoshows.cn";
    public static final String PROVIDER_ID = "here";

    /**
     * Create a new {@link HereServiceProvider}.
     *
     * @param clientId
     * @param clientSecret
     */
    public HereServiceProvider(String clientId, String clientSecret) {
        super(new OAuth2Template(clientId, clientSecret, BASE_URL+"/oauth/authorize", BASE_URL+"/oauth/token"));
        ((OAuth2Template)getOAuthOperations()).setUseParametersForClientAuthentication(true);
    }

    @Override
    public HereApi getApi(String accessToken) {
        return new HereApiTemplate(accessToken);
    }
}
