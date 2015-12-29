package com.mdtech.social.api.impl;

import com.mdtech.social.api.AlbumOperations;
import com.mdtech.social.api.PanoramioOperations;
import com.mdtech.social.api.PhotoOperations;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.UserOperations;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.ClientHttpRequestFactorySelector;

/**
 * Created by any on 2014/10/29.
 */
public class HereApiTemplate extends AbstractOAuth2ApiBinding implements HereApi {

    private UserOperations userOperations;
    private AlbumOperations albumOperations;
    private PanoramioOperations panoramioOperations;
    private PhotoOperations photoOperations;

    public HereApiTemplate(String accessToken) {
        super(accessToken);
        initialize();
    }

    @Override
    public AlbumOperations albumOperations() {
        return albumOperations;
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

    @Override
    protected FormHttpMessageConverter getFormMessageConverter() {
        FormHttpMessageConverter formHttpMessageConverter = super.getFormMessageConverter();
        formHttpMessageConverter.addPartConverter(getJsonMessageConverter());
        return formHttpMessageConverter;
    }

    // private helpers
    private void initialize() {
        // Wrap the request factory with a BufferingClientHttpRequestFactory so that the error handler can do repeat reads on the response.getBody()
        super.setRequestFactory(ClientHttpRequestFactorySelector.bufferRequests(getRestTemplate().getRequestFactory()));
        initSubApis();
    }

    private void initSubApis() {
        userOperations = new UserTemplate(getRestTemplate(), isAuthorized());
        albumOperations = new AlbumTemplate(getRestTemplate(), isAuthorized());
        panoramioOperations = new PanoramioTemplate(getRestTemplate(), isAuthorized());
        photoOperations = new PhotoTemplate(getRestTemplate(), isAuthorized());
    }
}
