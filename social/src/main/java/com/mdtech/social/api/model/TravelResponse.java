package com.mdtech.social.api.model;

import java.util.List;

/**
 * Created by any on 2014/10/24.
 */
public class TravelResponse extends AbstractResponse {

    private List<Travel> travels = null;

    private Travel travel;
    private com.mdtech.social.api.model.TravelSpot spot;

    public List<Travel> getTravels() {
        return travels;
    }

    public void setTravels(List<Travel> travels) {
        this.travels = travels;
    }

    public Travel getTravel() {
        return travel;
    }

    public void setTravel(Travel travel) {
        this.travel = travel;
    }

    public com.mdtech.social.api.model.TravelSpot getSpot() {
        return spot;
    }

    public void setSpot(com.mdtech.social.api.model.TravelSpot spot) {
        this.spot = spot;
    }
}
