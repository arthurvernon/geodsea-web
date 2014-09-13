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
    private Organisation organsation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="zone", column = @Column(name="ZONE")),
            @AttributeOverride(name="zoneTitle", column = @Column(name="ZONE_TITLE"))
    })
    private Zone zone;

    public OrganisationRole() {
    }

    public OrganisationRole(Organisation organisation, Zone zone) {
        this.organsation = organisation;
        this.zone = zone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Organisation getOrgansation() {
        return organsation;
    }

    public void setOrgansation(Organisation organsation) {
        this.organsation = organsation;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

}
