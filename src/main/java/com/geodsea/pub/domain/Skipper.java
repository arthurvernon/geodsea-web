package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * A permission, granted by an owner to be able to use the vessel.
 * <p>Rights such as this are temporal - they are granted and may equally so be revoked.</p>
 */
@Entity
@Table(name = "T_SKIPPER", schema = "BOAT", uniqueConstraints =
@UniqueConstraint(columnNames = {"PERSON_ID", "VESSEL_ID"}))
public class Skipper {

    @Id
    @GeneratedValue(generator = "SKIPPER_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SKIPPER_SEQ_GEN", sequenceName = "BOAT.SKIPPER_ID_SEQ")
    private Long id;


    /**
     * the person granted access to the vessel.
     */
    @ManyToOne()
    @JoinColumn(nullable = false, name = "PERSON_ID")
    private Person person;

    /**
     * The vessel that this person has a right to skipper.
     */
    @ManyToOne
    @JoinColumn(nullable = false, name = "VESSEL_ID")
    private Vessel vessel;

    /**
     * From when the right to use the boat applies.
     * <p>
     * If not specified then the right is assumed to exist from the date the right was granted.
     * </p>
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GRANTED_FROM", nullable = true)
    private Date grantedFrom;

    /**
     * Optional date up to which the person is granted a right to use the boat.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GRANTED_TO", nullable = true)
    private Date grantedTo;

    /**
     * A usage right may be suspended, overriding any period for which the usage right would otherwise be valid.
     */
    @Column(name = "SUSPENDED", nullable = false)
    private boolean suspended;

    public Skipper() {
    }

    /**
     * Create a new skipper that is not suspended and has not time constraints.
     *
     * @param vessel non-null vessel that the person is being given the right to use.
     * @param person non-null person that is the skipper
     */
    public Skipper(Vessel vessel, Person person) {
        this.person = person;
        this.vessel = vessel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public Date getGrantedFrom() {
        return grantedFrom;
    }

    public void setGrantedFrom(Date grantedFrom) {
        this.grantedFrom = grantedFrom;
    }

    public Date getGrantedTo() {
        return grantedTo;
    }

    public void setGrantedTo(Date grantedTo) {
        this.grantedTo = grantedTo;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    public boolean active() {
        Date now = new Date();
        return (!suspended)
                && (grantedFrom == null || grantedFrom.after(now))
                && (grantedTo == null || grantedTo.before(now));
    }
}