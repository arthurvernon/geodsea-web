package com.geodsea.pub.web.rest.dto;

import java.util.List;

public class GroupDTO extends ParticipantDAO {



    /**
     * Username of the contact person
     */
    private String contactLogin;

    /**
     * Output only.
     * <p>If the contact person needs to be updated then this must be done with a specific service call.</p>
     */
    private UserDTO contactPerson;

    public GroupDTO() {
    }

    public GroupDTO(Long groupId, boolean enabled, String login, String email, String contactLogin, UserDTO contactPerson) {
        super(groupId, login, enabled, email);
        this.contactPerson = contactPerson;
        this.contactLogin = contactLogin;
    }

    public UserDTO getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(UserDTO contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactLogin() {
        return contactLogin;
    }

    public void setContactLogin(String contactLogin) {
        this.contactLogin = contactLogin;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "contactLogin='" + contactLogin + '\'' +
                ", contactPerson=" + contactPerson +
                "} " + super.toString();
    }
}
