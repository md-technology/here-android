package com.mdtech.social.api.model;

import java.io.Serializable;

/**
 * Created by tiwen.wang on 7/8/2015.
 */
public class Location implements Serializable {

    private double[] position;
    private double alt;
    private String address;

    public double[] getPosition() {
        return position;
    }

    public void setPosition(double[] position) {
        this.position = position;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
