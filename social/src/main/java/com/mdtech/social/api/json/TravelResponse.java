package com.mdtech.social.api.json;

import java.util.List;

/**
 * Created by any on 2014/10/24.
 */
public class TravelResponse extends AbstractResponse {

    private List<Travel> travels = null;

    private Travel travel;
    private TravelSpot spot;

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

    public TravelSpot getSpot() {
        return spot;
    }

    public void setSpot(TravelSpot spot) {
        this.spot = spot;
    }
}
