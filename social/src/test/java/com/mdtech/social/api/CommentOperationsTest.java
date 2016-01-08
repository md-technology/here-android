/*
 * Copyright (C) 2016 The Here Android Project
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

import com.mdtech.social.api.model.Comment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.math.BigInteger;
import java.util.List;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 1/8/2016.
 */
public class CommentOperationsTest extends AbstractOperationsTest {
    private Log log = LogFactory.getLog(CommentOperationsTest.class);

    private CommentOperations commentOperations;

    @Before
    public void setup() {
        super.setup();
        super.authenticateClient();
        commentOperations = connectionRepository.findPrimaryConnection(HereApi.class).getApi().commentOperations();
    }

    @Test
    public void testGet() {
        BigInteger id = new BigInteger("26661658678236828921430818235");
        List<Comment> comments = commentOperations.get(CommentOperations.CommentType.photo, id, 10, 0 );

        Assert.assertTrue("Something wrong", comments.size() > 0);
    }

    @Test
    public void testCreate() {
        login();
        commentOperations = connectionRepository.findPrimaryConnection(HereApi.class).getApi().commentOperations();
        BigInteger id = new BigInteger("26661658678236828921430818235");
        String content = "Test content";

        Comment comment = null;
        try {
            comment = commentOperations.create(CommentOperations.CommentType.photo, id, content);
        }catch (HttpStatusCodeException ex) {
            ex.printStackTrace();
            log.error(ex.getResponseBodyAsString());
        }

        Assert.assertNotNull(comment);
        Assert.assertEquals("Something wrong", content, comment.getContent());

    }
}
