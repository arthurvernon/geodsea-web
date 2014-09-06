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
public class Rescue {

    @Id
    @GeneratedValue(generator = "RESCUE_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "RESCUE_SEQ_GEN", sequenceName = "BOAT.RESCUE_ID_SEQ")
    private Long id;

    /**
     * A group may perform rescue activities from a number of locations, e.g. from Fremantle and from Rottnest.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="ORGANISATION_FK", referencedColumnName = "ORGANISATION_ID")
    private Organisation organisation;

    /**
     * For info see <a href="http://en.wikipedia.org/wiki/Call_sign>Call sign on Wikipedia</a>
     */
    @NotNull
    @Size(min=4, max = 40)
    @Column(name="callsign")
    private String callsign;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="zone", column = @Column(name="ZONE")),
            @AttributeOverride(name="zoneTitle", column = @Column(name="ZONE_TITLE"))
    })
    private Zone zone;

    public Rescue()
    {
        super();
    }

    public Rescue(Organisation organisation, String callsign, String zoneTitle, Polygon zone) {
        super();
        this.zone = new Zone(zoneTitle, zone);
        this.callsign = callsign;
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

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

}
