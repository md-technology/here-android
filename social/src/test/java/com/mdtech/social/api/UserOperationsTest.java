/*
 * Copyright (C) 2015 The Here Android Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mdtech.social.api;

import com.mdtech.social.api.model.Album;
import com.mdtech.social.api.model.Photo;
import com.mdtech.social.api.model.User;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/18/2015.
 */
public class UserOperationsTest extends AbstractOperationsTest {
    private Log log = LogFactory.getLog(UserOperationsTest.class);

    private UserOperations userOperations;

    @Before
    public void setup() {
        super.setup();
        super.authenticateClient();
        userOperations = connectionRepository.findPrimaryConnection(HereApi.class).getApi().userOperations();
    }

    @Test
    public void testGet() {
        BigInteger id = new BigInteger("26449692454748190120520877226");
        User user = userOperations.get(id);

        Assert.assertEquals("user", user.getUsername());

        log.info(user.getAvatar().getOssKey());
    }

    @Test
    public void testGetGroup() {
        BigInteger id = new BigInteger("26494317932567731623693957073");
        User user = userOperations.get(id);

//        Assert.assertEquals("user", user.getMembers().size());

        Assert.assertNotNull(user.getMembers());

        log.info(user.getMembers().size());
    }

    @Test
    public void testGetPhotos() {
        BigInteger id = new BigInteger("26449692454748190120520877226");
        List<Photo> photos = userOperations.getPhotos(id, 10, 0);

        log.info(photos.size());

    }

    @Test
    public void testGetAlbums() {
        BigInteger id = new BigInteger("26449692454748190120520877226");
        List<Album> albums = userOperations.getAlbums(id, 10, 0);

        log.info(albums.size());
    }

}
