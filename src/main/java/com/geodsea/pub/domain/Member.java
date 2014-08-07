package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * A member is a {@link Participant participant} who has a role (details TBD) within an
 * {@link ParticipantGroup organsation}.
 * <p>
 * A member's ability to do stuff is predicated on the person being {@link #active} and if the {@link #memberSince}
 * amd {@link #memberUntil} date ranges are set, then the current date is within those lower and upper bounds (respectively).
 * </p>
 */
@Entity
@Table(name = "T_MEMBER", schema = "BOAT")
public class Member {

    @Id
    @GeneratedValue(generator = "MEMBER_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "MEMBER_SEQ_GEN", sequenceName = "BOAT.MEMBER_ID_SEQ")
    private Long id;

    /**
     * The person who is a member of the organisation.
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "PARTICIPANT_ID")
    private Participant participant;

    /**
     * The group to which this person is a member.
     */
    @ManyToOne(cascade = CascadeType.DETACH )
    @JoinColumn(nullable = false, name = "PARTICIPANT_GROUP_ID")
    private ParticipantGroup participantGroup;

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

    public Member(Participant participant, ParticipantGroup participantGroup, boolean active, boolean manager, Date memberSince, Date memberUntil) {
        super();
        this.participant = participant;
        this.participantGroup = participantGroup;
        this.active = active;
        this.memberSince = memberSince;
        this.manager = manager;
        this.memberUntil = memberUntil;
    }

    public ParticipantGroup getParticipantGroup() {
        return participantGroup;
    }

    public void setParticipantGroup(ParticipantGroup participantGroup) {
        this.participantGroup = participantGroup;
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
}