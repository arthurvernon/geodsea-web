package com.geodsea.pub.web.rest.dto;

import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.Vessel;

import javax.persistence.*;
import java.util.Date;

/**
 * A permission, granted by an owner to be able to use the vessel.
 * <p>Rights such as this are temporal - they are granted and may equally so be revoked.</p>
 */
public class SkipperDTO {

    private Long id;


    /**
     * the person granted access to the vessel.
     */
    private ParticipantDTO person;

    /**
     * The vessel that this person has a right to skipper.
     */
    private VesselDTO vessel;

    /**
     * From when the right to use the boat applies.
     * <p>
     * If not specified then the right is assumed to exist from the date the right was granted.
     * </p>
     */
    private Date grantedFrom;

    /**
     * Optional date up to which the person is granted a right to use the boat.
     */
    private Date grantedTo;

    /**
     * A usage right may be suspended, overriding any period for which the usage right would otherwise be valid.
     */
    private boolean suspended;

    public SkipperDTO() {
    }

    public SkipperDTO(Long id, ParticipantDTO person, VesselDTO vessel, Date grantedFrom, Date grantedTo, boolean suspended) {
        this.id = id;
        this.person = person;
        this.vessel = vessel;
        this.grantedFrom = grantedFrom;
        this.grantedTo = grantedTo;
        this.suspended = suspended;
    }

    public Long getId() {
        return id;
    }

    public ParticipantDTO getPerson() {
        return person;
    }

    public VesselDTO getVessel() {
        return vessel;
    }

    public Date getGrantedFrom() {
        return grantedFrom;
    }

    public Date getGrantedTo() {
        return grantedTo;
    }

    public boolean isSuspended() {
        return suspended;
    }
}