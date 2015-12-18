package com.mdtech.social.api;

import com.mdtech.social.api.model.Photo;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;
import org.springframework.social.connect.Connection;
import org.springframework.social.oauth2.AccessGrant;

import java.util.List;

/**
 * Created by any on 2014/10/31.
 */
public class PanoramioOperationsTest extends AbstractOperationsTest {
    private Log log = LogFactory.getLog(PanoramioOperationsTest.class);

    PanoramioOperations panoramioOperations;

    @Before
    public void before() {
        super.setup();

        AccessGrant accessGrant = connectionFactory.getOAuthOperations().authenticateClient("read");

        assertNotNull(accessGrant);

        assertNotNull(accessGrant.getAccessToken());

        Connection<HereApi> connection = connectionFactory.createConnection(accessGrant);
        HereApi hereApiApi = connection.getApi();
        panoramioOperations = hereApiApi.panoramioOperations();

    }

    @Test
    public void testGet() {

        assertNotNull(panoramioOperations);

        List<Photo> photos = panoramioOperations.getPanoramio("31", "121", "34", "122", "18");
        assertNotNull(photos);

        log.info(photos.size());

        for(Photo photo : photos) {
            log.info(photo.getId());
            log.info(photo.getTitle());
        }
    }
}
