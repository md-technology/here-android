package com.mdtech.social.api;

import com.mdtech.social.api.model.Photo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by tiwen.wang on 11/18/2014.
 */
public class PhotoOperationsTest extends AbstractOperationsTest {

    private Log log = LogFactory.getLog(PhotoOperationsTest.class);

    private PhotoOperations photoOperations;

    @Before
    public void setup() {
        super.setup();
        super.login();

        photoOperations = connectionRepository.findPrimaryConnection(HereApi.class).getApi().photoOperations();
    }

    @Test
    public void testGet() {
        String id = "26494372977652047572995979284";
        Photo photo = photoOperations.get(id);
        assertNotNull(photo);
    }

    @Test
    public void testUpload() {
        String filePath = "C:\\dev\\data\\thumb.jpg";
//        PhotoResponse photoResponse = photoOperations.upload("33", "110", "山东", "google", filePath);
    }
}
