package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Polygon;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

/**
 * An activity, performed by a Government agency, to confirm the authenticity of boating licenses within a particular jurisdiction.
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
    @Column(name = "WS_URL_LICENSE_CHECK", nullable = true, length = 100)
    private String boatLicenceWebServiceURL;

    /**
     * The license checks performed within the context of this system.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "licensor")
//    @JoinColumn(name="ROLE_LICENSING_ID")
    private List<License> licenses;

    /**
     * The region over which the licensing authority has jurisdiction.
     */
    @Column(name="JURISDICTION", nullable = false)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Polygon jurisdiction;

    public Licensor() {
        super();
    }

    public ParticipantGroup getParticipant() {
        return participant;
    }

    public void setParticipant(ParticipantGroup participant) {
        this.participant = participant;
    }

    public String getBoatLicenceWebServiceURL() {
        return boatLicenceWebServiceURL;
    }

    public void setBoatLicenceWebServiceURL(String boatLicenceWebServiceURL) {
        this.boatLicenceWebServiceURL = boatLicenceWebServiceURL;
    }

    public List<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }

    public Polygon getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Polygon jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}