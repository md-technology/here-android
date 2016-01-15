package com.mdtech.social.api.impl;

import com.mdtech.social.api.AlbumOperations;
import com.mdtech.social.api.CommentOperations;
import com.mdtech.social.api.LikeOperations;
import com.mdtech.social.api.PanoramioOperations;
import com.mdtech.social.api.PhotoOperations;
import com.mdtech.social.api.HereApi;
import com.mdtech.social.api.SignupOperations;
import com.mdtech.social.api.TrackOperations;
import com.mdtech.social.api.UserOperations;
import com.mdtech.social.api.model.Comment;
import com.mdtech.social.api.model.Track;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.support.ClientHttpRequestFactorySelector;

/**
 * Created by Tiven.wang on 2014/10/29.
 */
public class HereApiTemplate extends AbstractOAuth2ApiBinding implements HereApi {

    private SignupOperations signupOperations;
    private UserOperations userOperations;
    private AlbumOperations albumOperations;
    private PanoramioOperations panoramioOperations;
    private PhotoOperations photoOperations;
    private CommentOperations commentOperations;
    private LikeOperations likeOperations;
    private TrackOperations trackOperations;

    public HereApiTemplate(String accessToken) {
        super(accessToken);
        initialize();
    }

    @Override
    public SignupOperations signupOperations() {
        return signupOperations;
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
    public CommentOperations commentOperations() {
        return commentOperations;
    }

    @Override
    public LikeOperations likeOperation() {
        return likeOperations;
    }

    @Override
    public TrackOperations trackOperations() {
        return trackOperations;
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
        signupOperations = new SignupTemplate(getRestTemplate(), isAuthorized());
        userOperations = new UserTemplate(getRestTemplate(), isAuthorized());
        albumOperations = new AlbumTemplate(getRestTemplate(), isAuthorized());
        panoramioOperations = new PanoramioTemplate(getRestTemplate(), isAuthorized());
        photoOperations = new PhotoTemplate(getRestTemplate(), isAuthorized());
        commentOperations = new CommentTemplate(getRestTemplate(), isAuthorized());
        likeOperations = new LikeTemplate(getRestTemplate(), isAuthorized());
        trackOperations = new TrackTemplate(getRestTemplate(), isAuthorized());
    }
}
