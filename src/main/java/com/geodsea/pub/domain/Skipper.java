package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.List;

/**
 * A person who may hold any number of skipper licenses from the relevant authority (if
 * applicable).
 * <p>If there is no requirement for a license within a particular region, then the license will
 * in theory not exist.</p>
 * <p>Assumption is that a skipper will only hold one license with one licensing
 * authority. This assumption needs to be tested.</p>
 */
@Entity
@Table(name = "T_SKIPPER", schema = "BOAT")
public class Skipper {

    @Id
    @GeneratedValue(generator = "SKIPPER_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SKIPPER_SEQ_GEN", sequenceName = "BOAT.SKIPPER_ID_SEQ")
    private long id;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "skipper")
	private List<Trip> trips;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "skipper")
	private List<LicenseSkipper> licenses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "skipper")
	private List<VesselPermit> permits;

    @OneToOne(optional = false)
    @JoinColumn(name="PERSON_FK")
	private Person person;

	public Skipper(){
    super();
	}

    /**
     * Create a skipper with the minimum amount of information.
     * @param person
     */
    public Skipper(Person person)
    {
        this.person = person;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public List<LicenseSkipper> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<LicenseSkipper> licenses) {
        this.licenses = licenses;
    }

    public List<VesselPermit> getPermits() {
        return permits;
    }

    public void setPermits(List<VesselPermit> permits) {
        this.permits = permits;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}