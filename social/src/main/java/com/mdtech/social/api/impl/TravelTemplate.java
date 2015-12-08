package com.mdtech.social.api.impl;

import com.mdtech.social.api.TravelOperations;
import com.mdtech.social.api.json.Travel;
import com.mdtech.social.api.json.TravelResponse;

import org.springframework.web.client.RestTemplate;

/**
 * Created by any on 2014/10/31.
 */
public class TravelTemplate extends AbstractPonmapOperations implements TravelOperations {

    private final RestTemplate restTemplate;

    public TravelTemplate(RestTemplate restTemplate, boolean isAuthorized) {
        super(isAuthorized);
        this.restTemplate = restTemplate;
    }

    @Override
    public Travel getTravel(Long id) {
        TravelResponse travelResponse = this.restTemplate.getForObject(BASE_API_URL + "/travel/{id}", TravelResponse.class, id);
        return travelResponse.getTravel();
    }
}
