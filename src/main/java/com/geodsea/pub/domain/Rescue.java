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
    @JoinColumn(name="PARTICIPANT_ID", referencedColumnName = "ID")
    private ParticipantGroup participant;

    /**
     * For info see <a href="http://en.wikipedia.org/wiki/Call_sign>Call sign on Wikipedia</a>
     */
    @NotNull
    @Size(min=4, max = 40)
    @Column(name="callsign")
    private String callsign;

    /**
     * The region over which the sea rescue authority operates.
     */
    @Column(name="RESCUE_ZONE", nullable = false)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Polygon rescueZone;

    public Rescue()
    {
        super();
    }

    public Rescue(ParticipantGroup participantGroup, String callsign, Polygon rescueZone) {
        super();
        this.rescueZone = rescueZone;
        this.callsign = callsign;
        this.participant = participantGroup;
    }

    public ParticipantGroup getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantGroup participant) {
        this.participant = participant;
    }

    public Polygon getRescueZone() {
        return rescueZone;
    }

    public void setRescueZone(Polygon rescueZone) {
        this.rescueZone = rescueZone;
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
