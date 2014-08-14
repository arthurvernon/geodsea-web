package com.geodsea.pub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geodsea.pub.domain.util.DateConstants;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Any individual or organisation known to the system that can create resources.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "T_PARTICIPANT", schema = "BOAT", uniqueConstraints = { @UniqueConstraint(name = "uq_participant_name", columnNames = {"PARTICIPANT_NAME"})})
public abstract class Participant extends AbstractAuditingEntity implements Serializable {

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
    @Column(name = "PARTICIPANT_NAME", length = 50, nullable = false)
    @NotNull
    @Size(min=2, max = 50)
    private String participantName;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "T_PARTICIPANT_AUTHORITY", schema = "BOAT",
            joinColumns = {@JoinColumn(name = "participant_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "name", referencedColumnName = "name")})
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> authorities;

    protected Participant() {
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
    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
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

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

}
