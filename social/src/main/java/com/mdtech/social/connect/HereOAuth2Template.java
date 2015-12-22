package com.mdtech.social.connect;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;

import java.util.Map;

/**
 * Created by any on 2014/10/31.
 */
public class HereOAuth2Template extends OAuth2Template {

    private Log log = LogFactory.getLog(HereOAuth2Template.class);

    public HereOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
    }

    public HereOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String authenticateUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, authenticateUrl, accessTokenUrl);
    }

    public AccessGrant exchangeImplicitForAccess() {
        OAuth2Parameters params = new OAuth2Parameters();
        params.setScope("read");
        String oauthUrl = buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, params);
        log.debug("implicit oauth url = " + oauthUrl);
        Map map = getRestTemplate().postForObject(oauthUrl, null, Map.class);
        getRestTemplate().postForObject(buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, params ), null, Map.class);
        return postForAccessGrant(buildAuthenticateUrl(GrantType.IMPLICIT_GRANT, params ), null);
    }
}
