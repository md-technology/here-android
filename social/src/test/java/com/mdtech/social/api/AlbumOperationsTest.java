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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdtech.geojson.Feature;
import com.mdtech.geojson.FeatureCollection;
import com.mdtech.geojson.Point;
import com.mdtech.geojson.Properties;
import com.mdtech.social.api.model.Album;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigInteger;

/**
 * TODO insert class's header comments
 * Created by Tiven.wang on 12/11/2015.
 */
public class AlbumOperationsTest extends AbstractOperationsTest {

    private Log log = LogFactory.getLog(AlbumOperationsTest.class);

    private AlbumOperations albumOperations;

    private Album album;

    @Before
    public void setup() {
        super.setup();
        authenticateClient();
        albumOperations = connectionRepository.findPrimaryConnection(HereApi.class).getApi().albumOperations();
    }

    @After
    public void setdown() {
        if( null != album && null != album.getId()) {
            albumOperations.delete(album.getId());
        }
    }

    private void setupLogin() {
        super.login();
        albumOperations = connectionRepository.findPrimaryConnection(HereApi.class).getApi().albumOperations();
    }

    @Test
    public void testGet() {

        BigInteger id = new BigInteger("26750881779292192047881277021");
        Album album = albumOperations.get(id);

        assertEquals(id, album.getId());
        assertEquals("Something is wrong", "FeatureCollection", album.getFeatureCollection().getType());

        assertEquals("{}", album.getFeatureCollection().getProperties().get("style").asText());

        log.info(album.getFeatureCollection().getProperties());

        log.info(album.getPhotos().size());
    }

    @Test
    public void testCreate() {
        setupLogin();
        Album album = new Album();
        album.setType("FeatureCollection");
        album.setName("Sanqing-Shan2");
        album.setTitle("三清山mountain");

        Album res = null;
        try {
            res = albumOperations.create(album);
        }catch (HttpStatusCodeException ex) {
            ex.printStackTrace();
            log.error(ex.getResponseBodyAsString());
        }
        this.album = res;

        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals("Something is wrong", album.getType(), res.getType());
        assertEquals("Something is wrong", album.getTitle(), res.getTitle());
        assertEquals("Something is wrong", album.getName().toLowerCase(), res.getName());
    }

    @Test
    public void testAddFeatures() throws IOException {
        setupLogin();

        ObjectMapper serializer = new ObjectMapper();
        BigInteger id = new BigInteger("26750881779292192047881277021");
        Album album = albumOperations.get(id);
        int size = album.getFeatureCollection().getFeatures().size();

        FeatureCollection featureCollection = new FeatureCollection();
        Properties p =new Properties();
        Point point = new Point(110.2944946, 31.47318153);
        Feature feature = new Feature(point);
        feature.setProperties(p);
        featureCollection.addFeature(feature);

        String s = serializer.writeValueAsString(featureCollection);
        log.info(s);
        try{
            album = albumOperations.addFeatures(id, featureCollection);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

        Assert.assertEquals("Size is wrong", size+1, album.getFeatureCollection().getFeatures().size());
    }
}
