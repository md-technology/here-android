package com.mdtech.social.api;

import com.mdtech.social.api.model.Image;
import com.mdtech.social.api.model.Location;
import com.mdtech.social.api.model.Photo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.math.BigInteger;

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
        Photo photo = photoOperations.get(new BigInteger(id));
        assertNotNull(photo);
    }

    @Test
    public void testUpload() {
        String filePath = "C:\\dev\\resource\\_G7A1016.JPG";

        Resource resource = new FileSystemResource(new File(filePath)) {
            public String getFilename() throws IllegalStateException {
                return "photo.jpg";
            };
        };

        Image image = new Image();
        image.setTitle("Test photo upload title");
        image.setDescription("Test photo upload desc");

        Location location = new Location();
        location.setPosition(new double[]{33D,120D});
        location.setAddress("Test photo upload address");
        try {
            Photo photo = photoOperations.upload(image, location, null, null, false, resource);

            Assert.assertNotNull(photo);
            Assert.assertNotNull(photo.getId());
            Assert.assertNotNull(photo.getOssKey());

            log.info(photo.getOssKey());
        }catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
