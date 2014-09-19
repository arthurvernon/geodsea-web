package com.geodsea.pub.web.rest.dto;

import java.util.Date;

public class MemberDTO {

    private Long id;

    private boolean manager;

    private boolean active;

    private Date memberSince;

    private Date memberUntil;

    private ParticipantDTO participant;

    private CollectiveDTO group;

    public MemberDTO() {
    }

    public MemberDTO(Long memberId, ParticipantDTO participant, CollectiveDTO group, boolean manager, boolean active, Date memberSince, Date memberUntil) {
        this.id = memberId;
        this.manager = manager;
        this.active = active;
        this.memberSince = memberSince;
        this.memberUntil = memberUntil;
        this.participant = participant;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public boolean isManager() {
        return manager;
    }

    public boolean isActive() {
        return active;
    }

    public Date getMemberSince() {
        return memberSince;
    }

    public Date getMemberUntil() {
        return memberUntil;
    }

    public ParticipantDTO getParticipant() {
        return participant;
    }

    public CollectiveDTO getGroup() {
        return group;
    }

    @Override
    public String toString() {
        return "MemberDTO{" +
                "id=" + id +
                ", manager=" + manager +
                ", active=" + active +
                ", memberSince=" + memberSince +
                ", memberUntil=" + memberUntil +
                ", participant=" + participant +
                ", group=" + group +
                '}';
    }
}
