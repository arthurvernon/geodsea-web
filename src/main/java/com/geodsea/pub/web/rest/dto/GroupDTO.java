package com.geodsea.pub.web.rest.dto;

/**
 *
 */
public class GroupDTO extends CollectiveDTO {

    public GroupDTO() {
    }

    public GroupDTO(Long groupId, String groupLogin, String groupName, String langKey, boolean enabled, String telephone,
                    String email, ParticipantDTO contact) {
        super(groupId, groupLogin, groupName, langKey, enabled, telephone, email, contact);
    }
}
