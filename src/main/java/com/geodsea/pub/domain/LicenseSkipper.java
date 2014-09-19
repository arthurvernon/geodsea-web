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
    @JoinColumn(name="PERSON_ID")
	private Person person;


	public LicenseSkipper(){
        super();
	}

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}