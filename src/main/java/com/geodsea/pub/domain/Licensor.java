package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Polygon;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * An activity, performed by a Government agency, to confirm the authenticity of boating licenses within a particular zone.
 * <p>
 * The intent is that such authorities can enter a web service end-point which the application can call
 * in order to validate the details of a boat entered by a boat owner.
 * </p>
 */
@Entity
@Table(name = "T_LICENSOR", schema = "BOAT")
public class Licensor {

    @Id
    @GeneratedValue(generator = "LICENSOR_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "LICENSOR_SEQ_GEN", sequenceName = "BOAT.LICENSOR_ID_SEQ")
    private Long id;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="PARTICIPANT_ID", referencedColumnName = "ID")
    private ParticipantGroup participant;

    /**
     * A URL used to connect to the web service that implements the license inquiry web service.
     * <p>
     *     If not specified (null) then this particular organisation does not support the
     *     verification of a boating license via a web service.
     * </p>
     */
    @Column(name = "LICENSE_WS_URL", nullable = true, length = 100)
    private String licenceWsURL;

    @Column(name = "LICENSE_WS_USERNAME", nullable = true, length = 20)
    private String licenseWsUsername;


    @Column(name = "LICENSE_WS_PASSWORD", nullable = true, length = 20)
    private String licenseWsPassword;

//    /**
//     * The license checks performed within the context of this system.
//     */
//    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "licensor")
//    private List<License> licenses;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="zone", column = @Column(name="ZONE")),
            @AttributeOverride(name="zoneTitle", column = @Column(name="ZONE_TITLE"))
    })
    private Zone zone;

    public Licensor() {
        super();
    }

    public Licensor(ParticipantGroup participant, String licenceWsURL, String zoneTitle, Polygon zone ) {
        this.participant = participant;
        this.licenceWsURL = licenceWsURL;
        this.zone = new Zone(zoneTitle, zone);
    }

    public ParticipantGroup getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantGroup participant) {
        this.participant = participant;
    }

    public String getLicenceWsURL() {
        return licenceWsURL;
    }

    public void setLicenceWsURL(String licenceWsURL) {
        this.licenceWsURL = licenceWsURL;
    }

//    public List<License> getLicenses() {
//        return licenses;
//    }
//
//    public void setLicenses(List<License> licenses) {
//        this.licenses = licenses;
//    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseWsPassword() {
        return licenseWsPassword;
    }

    public void setLicenseWsPassword(String licenseWsPassword) {
        this.licenseWsPassword = licenseWsPassword;
    }

    public String getLicenseWsUsername() {
        return licenseWsUsername;
    }

    public void setLicenseWsUsername(String licenseWsUsername) {
        this.licenseWsUsername = licenseWsUsername;
    }

}