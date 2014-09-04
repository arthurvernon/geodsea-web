package com.geodsea.pub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

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
    private Polygon zone;

    /**
     * The zoneDescription in described in the language of the provider
     */
    @Column(name = "ZONE_TITLE", nullable = true, length = 50)
    private String zoneTitle;

    public Zone(String title, Polygon zone) {
        this.zone = zone;
        this.zoneTitle = title;
    }

    public Zone() {
    }

    public Polygon getZone() {
        return zone;
    }

    public void setZone(Polygon zone) {
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
