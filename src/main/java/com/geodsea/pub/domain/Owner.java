package com.geodsea.pub.domain;

import javax.persistence.*;

/**
 * An boat may be owned by one or more participants.
 * <p>
 *     For the system to recognise the owner of a boat, the owner must be registered.
 * </p>
 */
@Entity
@Table(name="T_OWNER", schema = "BOAT", uniqueConstraints =
@UniqueConstraint(columnNames = {"VESSEL_ID", "PARTICIPANT_ID"}))
public class Owner {

    @Id
    @GeneratedValue(generator = "OWNER_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "OWNER_SEQ_GEN", sequenceName = "BOAT.OWNER_ID_SEQ")
    private Long id;

    @ManyToOne()
    @JoinColumn(nullable = false, name="VESSEL_ID")
	private Vessel vessel;

    @ManyToOne()
    @JoinColumn(nullable = false, name="PARTICIPANT_ID")
    private Participant participant;

	public Owner(){
        super();
	}

    public Owner(Vessel vessel, Participant participant) {
        super();
        this.vessel = vessel;
        this.participant = participant;
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

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}