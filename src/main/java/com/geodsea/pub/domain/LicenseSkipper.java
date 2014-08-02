package com.geodsea.pub.domain;

import javax.persistence.*;

/**
 * A license granted to a skipper.
 */
@Entity
@Table(name = "T_LICENSE_SKIPPER", schema = "BOAT")
@PrimaryKeyJoinColumn(name="LICENSE_ID")
public class LicenseSkipper extends License {

    /**
     * The skipper
     */
    @ManyToOne
    @JoinColumn(name="SKIPPER_ID")
	private Skipper skipper;


	public LicenseSkipper(){
        super();
	}

}