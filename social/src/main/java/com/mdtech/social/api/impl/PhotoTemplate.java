package com.mdtech.social.api.impl;

import com.mdtech.social.api.PhotoOperations;
import com.mdtech.social.api.model.Image;
import com.mdtech.social.api.model.Location;
import com.mdtech.social.api.model.Photo;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Set;

/**
 * Created by tiwen.wang on 11/18/2014.
 */
public class PhotoTemplate extends AbstractPonmapOperations implements PhotoOperations {

    private final RestTemplate restTemplate;

    public PhotoTemplate(RestTemplate restTemplate, boolean isAuthorized) {
        super(isAuthorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public Photo upload(Image image, Location location, Set<String> tags, String album, boolean is360, Resource file) {
        // URL Parameters
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>(5);
        parts.add("files[]", file);
        parts.add("image", image);
        parts.add("location", location);
        parts.add("tags", tags);
        parts.add("album", album);
        parts.add("is360", is360);

        // Post
        return restTemplate.postForObject(BASE_API_URL + "/photo/upload", parts, Photo.class);
    }

    @Override
    public Photo get(BigInteger id) {
        return restTemplate.getForObject(BASE_API_URL + "/photo/{id}", Photo.class, id);
    }
}
