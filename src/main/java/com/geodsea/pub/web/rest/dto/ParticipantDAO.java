package com.geodsea.pub.web.rest.dto;

/**
 * Created by Arthur Vernon on 13/09/2014.
 */
public class ParticipantDAO {
    private Long id;

    private String login;

    private boolean enabled;

    private String email;


    public ParticipantDAO() {
    }

    public ParticipantDAO(long id, String login, boolean enabled, String email) {
        this.login = login;
        this.id = id;
        this.enabled = enabled;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return "ParticipantDAO{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", enabled=" + enabled +
                ", email='" + email + '\'' +
                '}';
    }
}
