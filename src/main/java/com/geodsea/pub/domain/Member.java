package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * A member is a {@link Participant participant} who has a role (details TBD) within an
 * {@link Collective organsation}.
 * <p>
 * A member's ability to do stuff is predicated on the person being {@link #active} and if the {@link #memberSince}
 * amd {@link #memberUntil} date ranges are set, then the current date is within those lower and upper bounds (respectively).
 * </p>
 */
@Entity
@Table(name = "T_MEMBER", schema = "BOAT")
public class Member extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(generator = "MEMBER_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "MEMBER_SEQ_GEN", sequenceName = "BOAT.MEMBER_ID_SEQ")
    private Long id;

    /**
     * The person who is a member of the organisation.
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "PARTICIPANT_FK")
    private Participant participant;

    /**
     * The group to which this person is a member.
     */
    @ManyToOne()
    @JoinColumn(nullable = false, name = "COLLECTIVE_FK")
    private Collective collective;

    /**
     * Optional date from which this participant became a member of the organisation.
     */
    @Column(name = "MEMBER_SINCE", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date memberSince;

    /**
     * Optional date to which this participant remains a valid member of the organisation.
     */
    @Column(name = "MEMBER_UNTIL", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date memberUntil;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;


    @Column(name = "MANAGER", nullable = false)
    private boolean manager;

    public Member() {
        super();
    }

    public Member(Participant participant, Collective collective, boolean active, boolean manager, Date memberSince, Date memberUntil) {
        super();
        this.participant = participant;
        this.collective = collective;
        this.active = active;
        this.memberSince = memberSince;
        this.manager = manager;
        this.memberUntil = memberUntil;
    }

    /**
     * Create an active manager
     * @param participant
     * @param collective
     */
    public static Member createActiveManager(Participant participant, Collective collective) {
        return new Member(participant, collective, true, true, new Date(), null);
    }

    public Collective getCollective() {
        return collective;
    }

    public void setCollective(Collective collective) {
        this.collective = collective;
    }

    public Date getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(Date memberSince) {
        this.memberSince = memberSince;
    }

    public Date getMemberUntil() {
        return memberUntil;
    }

    public void setMemberUntil(Date memberUntil) {
        this.memberUntil = memberUntil;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }


    public boolean active()
    {
        if (! active)
            return false;
        Date now = new Date();
        if (memberUntil != null && memberUntil.before(now))
            return false;
        if (memberSince != null && memberSince.after(now))
            return false;

        return true;
    }
}
