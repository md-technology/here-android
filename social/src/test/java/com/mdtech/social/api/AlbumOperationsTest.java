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

import com.mdtech.social.api.json.Album;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.math.BigInteger;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public class AlbumOperationsTest extends AbstractOperationsTest {

    private Log log = LogFactory.getLog(AlbumOperationsTest.class);

    private AlbumOperations albumOperations;

    @Before
    public void setup() {
        super.setup();
        super.authenticateClient();
        albumOperations = connectionRepository.findPrimaryConnection(Ponmap.class).getApi().albumOperations();
    }

    @Test
    public void testGet() {
        BigInteger id = new BigInteger("26721875713082321527310143061");
        Album album = albumOperations.get(id);

        assertEquals(id, album.getId());
    }

}
