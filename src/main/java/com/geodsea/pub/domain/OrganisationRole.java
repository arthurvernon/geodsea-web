package com.geodsea.pub.domain;

import javax.persistence.*;

/**
 * Basics for a role
 */
@MappedSuperclass
public class OrganisationRole {

    @Id
    @GeneratedValue(generator = "ROLE_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "ROLE_SEQ_GEN", sequenceName = "BOAT.ROLE_ID_SEQ")
    private Long id;


    @ManyToOne()
    @JoinColumn(name = "ORGANISATION_FK", referencedColumnName = "ORGANISATION_ID")
    private Organisation organisation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="zone", column = @Column(name="ZONE")),
            @AttributeOverride(name="zoneTitle", column = @Column(name="ZONE_TITLE"))
    })
    private Zone zone;

    public OrganisationRole() {
    }

    public OrganisationRole(Organisation organisation, Zone zone) {
        this.organisation = organisation;
        this.zone = zone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

}
