package com.geodsea.pub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Any individual or organisation known to the system that can create resources.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "T_PARTICIPANT", schema = "BOAT", uniqueConstraints = {
        @UniqueConstraint(name = "uq_participant_login", columnNames = {"LOGIN"})})
public abstract class Participant extends AuditedAuthorities implements Serializable {

    @Id
    @GeneratedValue(generator = "PARTICIPANT_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "PARTICIPANT_SEQ_GEN", sequenceName = "BOAT.PARTICIPANT_ID_SEQ")
    @Column(name="ID")
    private Long id;

    @Column(name = "ENABLED", nullable = true)
    private boolean enabled;

    @Column(name="REGISTRATION_TOKEN_EXPIRES", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date registrationTokenExpires;

    @Column(name="REGISTRATION_TOKEN", nullable = true, length = 30)
    private String registrationToken;

    /**
     * This is the username as stored in the Users table, i.e. a foreign key.
     * <p>It is not maintained as a foreign key relationship specifically so that
     * the username can only be changed via a transaction where the value here and in the
     * User table are both updated.</p>
     */
    @Column(name = "LOGIN", length = 50, nullable = false)
    @NotNull
    @Size(min=2, max = 100)
    private String login;


    /**
     * The tracks that this participant is permitted to see.
     */
    @JsonIgnore
    @OneToMany
    @JoinColumn(name="PARTICIPANT_FK", referencedColumnName="ID")
    private Set<Monitor> monitors;

    @Email
    @NotNull
    @Size(min = 5, max = 100)
    private String email;

    /**
     * The ISO language code (lower-case)
     */
    @Size(min = 2, max = 5)
    @NotNull
    @Column(name = "lang_key", nullable = false)
    private String langKey;



    /**
     * Create a disabled participant
     */
    protected Participant() {
    }

    /**
     * Create a disabled participant with no authorities
     * @param login
     * @param email
     */
    protected Participant(String login, String email) {
        this.login = login;
        this.email = email;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    /**
     * Get the name of the participant.
     * <p>
     *     This is a unique identifier across both individuals and groups.
     * </p>
     * @return a non-null participant name.
     */
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Date getRegistrationTokenExpires() {
        return registrationTokenExpires;
    }

    public void setRegistrationTokenExpires(Date registrationTokenExpires) {
        this.registrationTokenExpires = registrationTokenExpires;
    }


    public String getRegistrationToken() {
        return registrationToken;
    }

    public void setRegistrationToken(String registrationToken) {
        this.registrationToken = registrationToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }


    @Override
    public String toString() {
        return "id=" + id +
                ", enabled=" + enabled +
                ", language=" + langKey +
                ", email='" + email + '\'';
    }

}
