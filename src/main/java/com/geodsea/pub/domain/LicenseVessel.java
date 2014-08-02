package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * A license granted to a vessel owner.
 */
@Entity
@Table(name = "T_LICENSE_VESSEL", schema="BOAT")
public class LicenseVessel extends License {

    /**
     * A given boat will typically have a license or no license, but nothing precludes a boat from having multiple
     * licenses, particularly if the boat travels between different jurisdictions and each requires the boat to have
     * a license in that jurisdiction.
     */
    @ManyToOne()
    @JoinColumn(nullable = false, name="VESSEL_ID")
	private Vessel vessel;

    /**
     * The number of persons the vessel is licensed to carry
     */
    @Column(name="MAX_PEOPLE", nullable = true)
    Integer maxPeople;

	public LicenseVessel(){

	}

    public LicenseVessel(Date validFrom, Date validTo, String licenseNumber, Licensor issuer, Vessel vessel, Integer maxPeople) {
        super(validTo, validFrom, licenseNumber, issuer);
        this.vessel = vessel;
        this.maxPeople = maxPeople;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public Integer getMaxPeople() {
        return maxPeople;
    }

    public void setMaxPeople(Integer maxPeople) {
        this.maxPeople = maxPeople;
    }
}