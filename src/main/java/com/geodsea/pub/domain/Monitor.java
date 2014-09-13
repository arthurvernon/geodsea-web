package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * The rights of a participant to view trip information.
 * <p>
 *     If start time is not specified, then the monitor will be effective from the trip actual start.
 *     The start time should otherwise be some time after commencement of the trip. If the start time
 *     is before the trip then this will have the effect of making the trip visible from the first
 *     location report.
 * </p>
 * <p>
 *     The finish time affects how far into the trip the monitor will be able to receive updates for and
 *     view the trip. The finish time may be before the trip end time, resulting in the trip no longer
 *     being visible to the monitor even though the trip has not yet finished. If no finish time is specified
 *     then when the trip finishes, the monitor will no longer be able to see the trip. A time after the trip
 *     completes, indicates that the monitor will be able to view the details of the trip after it has finished.
 * </p>
 */
@Entity
@Table(name = "T_MONITOR", schema = "BOAT")
public class Monitor {

    @Id
    @GeneratedValue(generator = "MONITOR", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "MONITOR", sequenceName = "BOAT.MONITOR_ID_SEQ")
    private long id;

    @ManyToOne
    @JoinColumn(name = "TRIP_FK", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "PARTICIPANT_FK", nullable = false, updatable = false)
    private Participant participant;

    /*
    * Access to the track is valid until the expiry time.
    * <p>
    *     If no expiry time is specified then the access remains until the track is deleted.
    * </p>
    */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_TIME", nullable = true)
    private Date startTime;

    /*
    * Access to the track is valid until the expiry time.
    * <p>
    *     If no expiry time is specified then the access remains until the track is deleted.
    * </p>
    */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "FINISH_TIME", nullable = true)
    private Date finishTime;

    public Monitor() {
    }

    /**
     * A monitor that is valid for the duration of the trip
     *
     * @param trip the trip to be monitored
     * @param participant the participant who is being provided access.
     */
    public Monitor(Trip trip, Participant participant) {
        this(trip, participant, null, null);
    }

    /**
     * A monitor that is valid from the start until the finish time.
     * @param trip
     * @param participant
     * @param startTime
     * @param finishTime
     */
    public Monitor(Trip trip, Participant participant, Date startTime, Date finishTime) {
        this.trip = trip;
        this.participant = participant;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }
}
