package com.geodsea.pub.web.rest.dto;

/**
 * Created by Arthur Vernon on 13/09/2014.
 */
public class ParticipantDTO {
    /**
     * ID of the participant or the role that the organisation performs
     */
    private Long id;

    private String login;

    private boolean enabled;

    private String email;

    private String langKey;

    /**
     * The person's or the groups name
     */
    private String name;

    private String telephone;

    public ParticipantDTO() {
    }

    /**
     *
     * @param id ID of the participant or the role that the organisation performs
     * @param login
     * @param enabled
     * @param name
     * @param email
     * @param langKey
     * @param telephone
     */
    public ParticipantDTO(Long id, String login, boolean enabled, String name, String email, String langKey, String telephone) {
        this.login = login;
        this.id = id;
        this.name = name;
        this.enabled = enabled;
        this.email = email;
        this.langKey = langKey;
        this.telephone = telephone;
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

    public String getLangKey() {
        return langKey;
    }

    public String getName() {
        return name;
    }

    public String getTelephone() {
        return telephone;
    }

    @Override
    public String toString() {
        return "ParticipantDTO{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", email='" + email + '\'' +
                ", langKey='" + langKey + '\'' +
                ", telephone='" + telephone + '\'' +
                '}';
    }
}
