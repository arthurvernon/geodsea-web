package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Polygon;

import javax.persistence.*;

/**
 * An organisation that undertakes events such as yacht races or fishing competitions and needs to be able
 * to monitor the location of boats for a given period of time.
 * <p>
 *     For the club to be able to monitor the location of a boat, it must be able to obtain the permission
 *     of the skipper.
 * </p>
 */
@Entity
@Table(name = "T_CLUB", schema = "BOAT")
public class Club extends OrganisationRole {

    public Club()
    {
        super();
    }

    public Club(Organisation organisation, String callsign, String zoneTitle, Polygon zone) {
        super(organisation, new Zone(zoneTitle, zone));
    }

}
