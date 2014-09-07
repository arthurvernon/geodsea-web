package com.geodsea.pub.web.rest.dto;

import java.util.List;

public class GroupDTO {

    private Long id;

    private String login;

    private String email;

    /**
     * Output only.
     * <p>If the contact person needs to be updated then this must be done with a specific service call.</p>
     */
    private UserDTO contactPerson;

    public GroupDTO() {
    }

    public GroupDTO(Long groupId, String login, String email, UserDTO contactPerson) {
        this.id = groupId;
        this.contactPerson = contactPerson;
        this.login = login;
        this.email = email;
    }


    public String getLogin() {
        return login;
    }


    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserDTO getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(UserDTO contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
