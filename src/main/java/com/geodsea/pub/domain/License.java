package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * Base class for defining licenses.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "T_LICENSE", schema = "BOAT")
public abstract class License {

    @Id
    @GeneratedValue(generator = "LICENSE_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "LICENSE_SEQ_GEN", sequenceName = "BOAT.LICENSE_ID_SEQ")
    private Long id;


    /**
     * The date from which the user or the authority specifies that the license is valid until.
     * <p>
     *     If the license is verified then this value may only be altered by the jurisdiction issuing the license.
     * </p>
     */
    @Column(name="VALID_FROM")
    @Temporal(TemporalType.DATE)
    private Date validFrom;

    /**
     * The date to which the user or the authority specifies that the license is valid until.
     * <p>
     *     If the license is verified then this value may only be altered by the jurisdiction issuing the license.
     * </p>
     */
    @Column(name="VALID_TO")
    @Temporal(TemporalType.DATE)
    private Date validTo;

    /**
     * "Immutable" license number for the license, that is if and when the license is verified by the licensing authority.
     * <p>
     *     Once verified then the license number may not be altered.
     * </p>
     */
    @Column(name="LICENSE_NUM", nullable = false, length = 30)
    private String licenseNumber;

    /**
     * The organisation role that granted the license
     */
    @ManyToOne()
    @JoinColumn(name="LICENSOR_ID")
    private Licensor licensor;

    /**
     * The date/time when the relevant authority verified the license.
     */
    @Column(name="VERIFIED_DT", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date verifiedDate;

    /**
     * The decision by the authority to verify or otherwise.
     * <p>
     *     If the authority has not considered the license then this will be null.
     * </p>
     */
    @Column(name="VERIFIED", nullable = true)
    private Boolean verified;


    public License()
    {
        super();
    }


    protected License(Date validTo, Date validFrom, String licenseNumber, Licensor licensor) {
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.licenseNumber = licenseNumber;
        this.licensor = licensor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Licensor getLicensor() {
        return licensor;
    }

    public void setLicensor(Licensor licensor) {
        this.licensor = licensor;
    }

    public Date getVerifiedDate() {
        return verifiedDate;
    }

    public void setVerifiedDate(Date d) {
        this.verifiedDate = d;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
