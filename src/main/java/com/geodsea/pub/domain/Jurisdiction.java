package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.MultiPolygon;

import javax.persistence.*;

/**
 * Activity typically performed by a sea rescue organisation.
 */
@Entity
@Table(name = "T_JURISDICTION", schema = "BOAT")
public class Jurisdiction extends OrganisationRole {

    public Jurisdiction()
    {
        super();
    }

    public Jurisdiction(Organisation organisation, String callsign, String zoneTitle, MultiPolygon zone) {
        super(organisation, new Zone(zoneTitle, zone));
    }
}
