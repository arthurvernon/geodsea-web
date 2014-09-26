package com.geodsea.pub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

/**
 * Audited entities that have authorities attached to them.
 */
@MappedSuperclass
public abstract class AuditedAuthorities extends AbstractAuditingEntity{

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "T_PARTICIPANT_AUTHORITY", schema = "BOAT",
            joinColumns = {@JoinColumn(name = "participant_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "name", referencedColumnName = "name")})
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> authorities;

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public void addAuthority(Authority a)
    {
        if (authorities == null)
            authorities = new HashSet<Authority>();
        authorities.add(a);
    }
    /**
     * Check if this participant has the authority to perform a role.
     * @param authority
     * @return
     */
    public boolean hasAuthority(String authority) {
        if (authorities == null)
            return false;
        for (Authority a : authorities)
            if (a.getName().equals(authority))
                return true;
        return false;
    }

}
