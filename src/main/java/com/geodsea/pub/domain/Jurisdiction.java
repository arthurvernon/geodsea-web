package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Polygon;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Activity typically performed by a sea rescue organisation.
 */
@Entity
@Table(name = "T_JURISDICTION", schema = "BOAT")
public class Jurisdiction {

    @Id
    @GeneratedValue(generator = "JURISDICTION_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "JURISDICTION_SEQ_GEN", sequenceName = "BOAT.JURISDICTION_ID_SEQ")
    private Long id;

    /**
     * A group may perform rescue activities from a number of locations, e.g. from Fremantle and from Rottnest.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="PARTICIPANT_ID", referencedColumnName = "ID")
    private ParticipantGroup participant;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="zone", column = @Column(name="ZONE")),
            @AttributeOverride(name="zoneTitle", column = @Column(name="ZONE_TITLE"))
    })
    private Zone zone;

    public Jurisdiction()
    {
        super();
    }

    public Jurisdiction(ParticipantGroup participantGroup, String callsign, String zoneTitle, Polygon zone) {
        super();
        this.zone = new Zone(zoneTitle, zone);
        this.participant = participantGroup;
    }

    public ParticipantGroup getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantGroup participant) {
        this.participant = participant;
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
