package com.geodsea.pub.web.rest.dto;

import java.util.Date;

public class MemberDTO {

    private Long id;

    private boolean manager;

    private boolean active;

    private Date memberSince;

    private Date memberUntil;

    private ParticipantDAO participant;

    public MemberDTO() {
    }

    public MemberDTO(Long id, boolean manager, boolean active, Date memberSince, Date memberUntil, ParticipantDAO participant) {
        this.id = id;
        this.manager = manager;
        this.active = active;
        this.memberSince = memberSince;
        this.memberUntil = memberUntil;
        this.participant = participant;
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
                '}';
    }
}
