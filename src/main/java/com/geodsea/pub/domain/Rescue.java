package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Polygon;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Activity typically performed by a sea rescue organisation.
 */
@Entity
@Table(name = "T_RESCUE", schema = "BOAT")
public class Rescue extends OrganisationRole {

    /**
     * For info see <a href="http://en.wikipedia.org/wiki/Call_sign>Call sign on Wikipedia</a>
     */
    @NotNull
    @Size(min=4, max = 40)
    @Column(name="callsign")
    private String callsign;


    public Rescue()
    {
        super();
    }

    public Rescue(Organisation organisation, String callsign, String zoneTitle, Polygon zone) {
        super(organisation, new Zone(zoneTitle, zone));
        this.callsign = callsign;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

}
