package com.geodsea.pub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * An address and corresponding geographic point.
 */
@Embeddable
public class Zone {

    /**
     * The region over which the sea rescue authority operates.
     */
    @Column(name = "ZONE", nullable = false)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private MultiPolygon  zone;

    /**
     * The zoneDescription in described in the language of the provider
     */
    @Column(name = "ZONE_TITLE", nullable = true, length = 50)
    private String zoneTitle;

    public Zone(String title, MultiPolygon zone) {
        this.zone = zone;
        this.zoneTitle = title;
    }

    public Zone() {
    }

    public MultiPolygon getZone() {
        return zone;
    }

    public void setZone(MultiPolygon zone) {
        this.zone = zone;
    }

    public String getZoneTitle() {
        return zoneTitle;
    }

    public void setZoneTitle(String zoneTitle) {
        this.zoneTitle = zoneTitle;
    }

    @Override
    public String toString() {
        return zoneTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Zone zone = (Zone) o;

        if (zoneTitle != null ? !zoneTitle.equals(zone.zoneTitle) : zone.zoneTitle != null)
            return false;

        return true;
    }

}
