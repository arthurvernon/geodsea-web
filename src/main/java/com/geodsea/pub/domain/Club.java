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
public class Club {

    @Id
    @GeneratedValue(generator = "CLUB_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "CLUB_SEQ_GEN", sequenceName = "BOAT.CLUB_ID_SEQ")
    private Long id;

    /**
     * A group may perform rescue activities from a number of locations, e.g. from Fremantle and from Rottnest.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="ORGANISATION_FK", referencedColumnName = "ORGANISATION_ID")
    private Organisation organisation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="zone", column = @Column(name="ZONE")),
            @AttributeOverride(name="zoneTitle", column = @Column(name="ZONE_TITLE"))
    })
    private Zone zone;

    public Club()
    {
        super();
    }

    public Club(Organisation organisation, String callsign, String zoneTitle, Polygon zone) {
        super();
        this.zone = new Zone(zoneTitle, zone);
        this.organisation = organisation;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
