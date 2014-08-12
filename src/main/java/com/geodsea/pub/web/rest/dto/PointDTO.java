package com.geodsea.pub.web.rest.dto;

/**
 * Created by Arthur Vernon on 12/08/2014.
 */
public class PointDTO {
    private double lat;
    private double lon;

    public PointDTO(double lat, double lon) {
        this.lat= lat;
        this.lon = lon;
    }

    public PointDTO()
    {
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    @Override
    public String toString() {
        return "PointDTO{" +
                "lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
