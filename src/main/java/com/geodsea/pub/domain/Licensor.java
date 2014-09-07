package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Polygon;

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
public class Licensor extends OrganisationRole {


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

    public Licensor() {
        super();
    }

    public Licensor(Organisation organisation, String licenceWsURL, String zoneTitle, Polygon zone ) {
        super(organisation, new Zone(zoneTitle, zone));
        this.licenceWsURL = licenceWsURL;
    }


    public String getLicenceWsURL() {
        return licenceWsURL;
    }

    public void setLicenceWsURL(String licenceWsURL) {
        this.licenceWsURL = licenceWsURL;
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