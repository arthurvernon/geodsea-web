package com.geodsea.pub.domain;

import javax.persistence.*;

/**
 * An boat may be owned by one or more participants.
 * <p>
 *     For the system to recognise the owner of a boat, the owner must be registered.
 * </p>
 */
@Entity
@Table(name="T_VESSEL_OWNER", schema = "BOAT", uniqueConstraints =
@UniqueConstraint(columnNames = {"BOAT_ID", "PARTICIPANT_ID"}))
public class VesselOwner {

    @Id
    @GeneratedValue(generator = "VESSEL_OWNER_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "VESSEL_OWNER_SEQ_GEN", sequenceName = "BOAT.VESSEL_OWNER_ID_SEQ")
    private Long id;

    @ManyToOne()
    @JoinColumn(nullable = false, name="BOAT_ID")
	private Vessel vessel;

    @ManyToOne()
    @JoinColumn(nullable = false, name="PARTICIPANT_ID")
    private Participant owner;

	public VesselOwner(){
        super();
	}

    public VesselOwner(Vessel vessel, Participant owner) {
        super();
        this.vessel = vessel;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public Participant getOwner() {
        return owner;
    }

    public void setOwner(Participant owner) {
        this.owner = owner;
    }
}