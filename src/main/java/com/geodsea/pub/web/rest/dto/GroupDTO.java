package com.geodsea.pub.web.rest.dto;

import com.geodsea.pub.domain.Participant;

public class GroupDTO extends ParticipantDTO {


    /**
     * Output only.
     * <p>If the contact person needs to be updated then this must be done with a specific service call.</p>
     */
    private ParticipantDTO contact;

    public GroupDTO() {
    }

    public GroupDTO(Long groupId, String groupLogin, String groupName, String langKey, boolean enabled, String telephone,
                    String email, ParticipantDTO contact) {
        super(groupId, groupLogin, enabled, groupName, email, langKey, telephone);
        this.contact = contact;
    }


    public ParticipantDTO getContact() {
        return contact;
    }


    @Override
    public String toString() {
        return "GroupDTO{" +
                ", contactPerson=" + contact +
                "} " + super.toString();
    }
}
