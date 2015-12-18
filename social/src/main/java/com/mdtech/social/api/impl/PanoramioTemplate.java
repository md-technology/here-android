package com.mdtech.social.api.impl;

import com.mdtech.social.api.PanoramioOperations;
import com.mdtech.social.api.model.Photo;

import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by any on 2014/10/31.
 */
public class PanoramioTemplate extends AbstractPonmapOperations implements PanoramioOperations {

    private final RestTemplate restTemplate;

    public PanoramioTemplate(RestTemplate restTemplate, boolean isAuthorized) {
        super(isAuthorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Photo> getPanoramio(String swLat, String swLng, String neLat, String neLng, String level) {
        return restTemplate.getForObject(BASE_API_URL + "/panoramio?swlat={swLat}&swlng={swLng}&nelat={neLat}&nelng={neLng}&level={level}",
                PhotoList.class,
                swLat, swLng, neLat, neLng, level);

    }

    @Override
    public List<Photo> getPanoramio(String... params) {
        return restTemplate.getForObject(BASE_API_URL + "/panoramio?swlat={swLat}&swlng={swLng}&nelat={neLat}&nelng={neLng}&level={level}",
                PhotoList.class,
                params);
    }

    @Override
    public List<Photo> search(String swLat, String swLng, String neLat, String neLng, String level, String width, String height, String term, String type) {
        Map<String, String> urlVariables = new HashMap<String, String>();
        return restTemplate.getForObject(BASE_API_URL + "/panoramio/search?swlat={swLat}&swlng={swLng}&nelat={neLat}&nelng={neLng}&level={level}&width={width}&height={height}&term={term}&type={type}",
                PhotoList.class,
                swLat, swLng, neLat, neLng, level, width, height, term, type
        );
    }
}
