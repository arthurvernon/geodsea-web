package com.geodsea.pub.domain;

import com.geodsea.pub.service.util.TripCreateChecks;
import com.geodsea.pub.service.util.TripUpdateChecks;
import com.vividsolutions.jts.geom.LineString;

import javax.persistence.*;
import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A trip created when the skipper and vessel both exist within the system.
 */
@Entity
@Table(name = "T_TRIP_SKIPPER", schema = "BOAT")
@PrimaryKeyJoinColumn(name="ID", referencedColumnName = "ID")
public class TripSkipper extends Trip {

    /**
     * When the skipper plans to start this trip.
     */
    @Column(name = "SCHEDULED_START", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @Future(groups = TripCreateChecks.class)
    private Date scheduledStartTime;

    /**
     * Who is the skipper for this trip.
     * <p>
     *     The skipper must be identified so that the trip record is associated with at least one person.
     * </p>
     */
    @ManyToOne(optional = false)
    @NotNull
    private Skipper skipper;

    /**
     * The frequency in seconds at which the vessel should report its location.
     * <p>
     *     Zero implies no obligation.
     * </p>
     */
    @Column(name="REPORT_RATE", nullable = false)
    @Min(value=0, groups= TripUpdateChecks.class)
    private int reportRate;

    /**
     * The particular vessel that is intended to be used for this trip.
     * <p>
     *     The vessel may change but rarely would.
     * </p>
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "VESSEL_ID", nullable = false)
    @NotNull
    private Vessel vessel;

    /**
     * A useful precis of the trip, entered probably after the fact.
     */
    @Column(name = "SUMMARY", nullable = true, length = 255)
    @Size(max = 255)
    private String summary;


    /**
     * Actual tracking information, ie time at location.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name="T_LOCATION_TIME", schema = "BOAT", joinColumns = @JoinColumn(name="TRIP_FK"))
    private List<LocationTime> locationTimes;


    public TripSkipper() {
        super();
    }

    /**
     * Create a plan for the trip
     * @param vessel the vessel being taken on the trip.
     * @param skipper the skipper in charge of the vessel
     * @param headline a summary of the purpose of the trip, shared with SRO
     * @param scheduledStartTime planned start time for the trip
     * @param scheduledEndTime planned end time for the trip
     * @param summary a personal summary of the trip, not shared with SRO
     * @param wayPoints optional way points for journey, beginning with the starting location
     * @param fuel (optional) number of litres of fuel. Required if vessel is motorised.
     * @param people number of people on board
     */
    public TripSkipper(Vessel vessel, Skipper skipper, String headline, Date scheduledStartTime, Date scheduledEndTime, String summary, LineString wayPoints, Integer fuel, int people) {
        super(headline, null, scheduledEndTime, wayPoints, fuel, people);
        this.summary = summary;
        this.scheduledStartTime = scheduledStartTime;
        this.vessel = vessel;
        this.skipper = skipper;
    }

    public Skipper getSkipper() {
        return skipper;
    }

    public void setSkipper(Skipper skipper) {
        this.skipper = skipper;
    }
    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }

    public List<LocationTime> getLocationTimes() {
        return locationTimes;
    }

    public void setLocationTimes(List<LocationTime> locationTimes) {
        this.locationTimes = locationTimes;
    }

    public int getReportRate() {
        return reportRate;
    }

    public void setReportRate(int reportRate) {
        this.reportRate = reportRate;
    }

    public Date getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(Date scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }


    /**
     * Append any locations to the existing set.
     * @param list
     */
    public void addAll(List<LocationTime> list) {
        if (list == null || list.size() == 0)
            return;

        if (locationTimes == null)
            locationTimes = list;
        else
            locationTimes.addAll(list);
    }

    public void add(LocationTime locationTime) {
        if (locationTime == null)
            return;
        if (locationTimes == null)
            locationTimes = new ArrayList<LocationTime>();
        locationTimes.add(locationTime);
    }
}