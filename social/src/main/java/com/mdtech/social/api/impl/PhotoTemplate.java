package com.mdtech.social.api.impl;

import com.mdtech.social.api.PhotoOperations;
import com.mdtech.social.api.json.Photo;

import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
    public Photo upload(String lat, String lng, String address, String vendor, String file) {

        // URL Parameters
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.add("files[]", new FileSystemResource(file));
        parts.add("lat", lat);
        parts.add("lng", lng);
        parts.add("address", address);
        parts.add("vendor", vendor);

        // Post
        return restTemplate.postForObject(BASE_API_URL + "/photo/upload", parts, Photo.class);
    }

    @Override
    public Photo get(String id) {
        return restTemplate.getForObject(BASE_API_URL + "/photo/{id}", Photo.class, id);
    }
}
