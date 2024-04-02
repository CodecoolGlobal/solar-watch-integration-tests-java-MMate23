package com.code.solarwatch.model;

import java.io.Serializable;

public class CoordinateByCity implements Serializable {

    private String name;

    private Object local_names;
    private String lon;

    private String lat;

    private String country;

    private String state;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getLocal_names() {
        return local_names;
    }

    public void setLocal_names(Object local_names) {
        this.local_names = local_names;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
