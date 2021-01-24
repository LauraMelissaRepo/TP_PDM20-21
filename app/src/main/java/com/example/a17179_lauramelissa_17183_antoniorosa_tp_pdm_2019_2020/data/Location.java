package com.example.a17179_lauramelissa_17183_antoniorosa_tp_pdm_2019_2020.data;

public class Location {

    private String locationDescription, lat, lng;

    public Location(){};

    public Location (String locationDescription, String lat, String lng){
        this.locationDescription = locationDescription;
        this.lat = lat;
        this.lng = lng;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }
}
