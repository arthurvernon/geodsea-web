package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * A Person who the owner has authorized to skipper a boat.
 * <p>Rights such as this are temporal - they are granted and may equally so be
 * revoked.</p>
 * <p>This may or may not be the owner. The owner may authorize any number
 * of people to skipper the boat. Additionally a skipper may be granted a right by more than one of the vessel's owners.</p>
 */
@Entity
@Table(name = "T_VESSEL_PERMIT", schema = "BOAT", uniqueConstraints =
@UniqueConstraint(columnNames = {"SKIPPER_ID", "BOAT_ID", "VESSEL_OWNER_ID"}))
public class VesselPermit {

    @Id
    @GeneratedValue(generator = "VESSEL_PERMIT_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "VESSEL_PERMIT_SEQ_GEN", sequenceName = "BOAT.VESSEL_PERMIT_ID_SEQ")
    private Long id;


    /**
     * Access to the person and any license that this person may have that entitles him to be in charge of a boat.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    private Skipper skipper;

    /**
     * The boat that this person has a right to skipper.
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "BOAT_ID")
    private Vessel vessel;

    /**
     * The person who granted this particular person a right to use the boat (and who may subsequently revoke this permission).
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "VESSEL_OWNER_ID")
    private VesselOwner grantedBy;

    /**
     * When this right was granted by the owner to the skipper.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="GRANTED_ON", nullable = true)
    private Date grantedOn;

    /**
     * From when the right to use the boat applies.
     * <p>
     * If not specified then the right is assumed to exist from the date the right was granted.
     * </p>
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="GRANTED_FROM", nullable = true)
    private Date grantedFrom;

    /**
     * Optional date up to which the person is granted a right to use the boat.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="GRANTED_TO", nullable = true)
    private Date grantedTo;

    /**
     * A usage right may be suspended, overriding any period for which the usage right would otherwise be valid.
     */
    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    public VesselPermit() {

    }

}