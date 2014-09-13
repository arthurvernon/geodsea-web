package com.geodsea.pub.web.rest.dto;

import com.geodsea.pub.domain.LocationTime;
import com.geodsea.pub.domain.Rescue;
import com.geodsea.pub.domain.Skipper;
import com.geodsea.pub.domain.Vessel;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A plan submitted by a boat (skipper) at the outset of a trip/journey.
 * <p>
 * The plan originates as a starting location, may define some intermediate points, and finishes at an endpoint.
 * The endpoint is typically the same as the start point, but need not be, e.g. in the case of a yacht race
 * or where there is a planned stopover and a new trip at some later time.
 * </p>
 */
public class TripDTO {

    private Long id;

    /**
     * The reason for making the trip, e.g. weekend at Rottnest.
     * <p>
     * This information is shared with the rescue authority.
     * </p>
     */
    private String headline;

    /**
     * A useful precis of the trip, entered probably after the fact.
     */
    private String summary;


    /**
     * The number of people including the skipper who are on the boat.
     * <p>The maximum number is not constrained, not even by the {@link com.geodsea.pub.domain.LicenseVessel#maxPeople}.</p>
     */
    private Integer peopleOnBoard;

    /**
     * When this trip is planned to start.
     */
    private Date scheduledStartTime;

    /**
     * When this trip actually started (if the fact was recorded).
     */
    private Date actualStartTime;

    private Integer fuelOnBoard;



    /**
     * The time in the plan when the journey is intended to be completed.
     */
    private Date scheduledEndTime;


    /**
     * The organisation that the system has ascertained is responsible for monitoring this trip.
     */
    private RescueOrganisationDTO rescueOrganisation;

    /**
     * The date when the plan was first advised to sea rescue.
     */
    private Date planFirstAdvised;

    /**
     * The date when the plan was last advised to sea rescue.
     * <p>
     * This will be different from when the plan was first advised if the details of the journey have been amended.
     * </p>
     */
    private Date planLastAdvised;

    /**
     * The actual time, if ever, that the journey was logged as completed.
     */
    private Date actualEndTime;

    /**
     * Who is the skipper for this trip.
     * <p>
     *     The skipper must be identified so that the trip record is associated with at least one person.
     * </p>
     */
    private SkipperDTO skipper;

    /**
     * The particular vessel that is intended to be used for this trip.
     * <p>
     *     The vessel may change but rarely would.
     * </p>
     */
    private Vessel vessel;


    public TripDTO() {
        super();
    }

    /**
     * Create a trip with the minimum required information.
     * @param vessel
     * @param skipper
     */
    public TripDTO(Vessel vessel, SkipperDTO skipper, String headline) {
        this.vessel = vessel;
        this.skipper = skipper;
        this.headline = headline;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getPeopleOnBoard() {
        return peopleOnBoard;
    }

    public void setPeopleOnBoard(Integer peopleOnBoard) {
        this.peopleOnBoard = peopleOnBoard;
    }

    public Date getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(Date scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Integer getFuelOnBoard() {
        return fuelOnBoard;
    }

    public void setFuelOnBoard(Integer fuelOnBoard) {
        this.fuelOnBoard = fuelOnBoard;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Date getScheduledEndTime() {
        return scheduledEndTime;
    }

    public void setScheduledEndTime(Date scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
    }

    public Date getPlanFirstAdvised() {
        return planFirstAdvised;
    }

    public void setPlanFirstAdvised(Date planFirstAdvised) {
        this.planFirstAdvised = planFirstAdvised;
    }

    public Date getPlanLastAdvised() {
        return planLastAdvised;
    }

    public void setPlanLastAdvised(Date planLastAdvised) {
        this.planLastAdvised = planLastAdvised;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueOrganisationDTO getRescueOrganisation() {
        return rescueOrganisation;
    }

    public void setRescueOrganisation(RescueOrganisationDTO rescueOrganisation) {
        this.rescueOrganisation = rescueOrganisation;
    }

    public SkipperDTO getSkipper() {
        return skipper;
    }

    public void setSkipper(SkipperDTO skipper) {
        this.skipper = skipper;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public void setVessel(Vessel vessel) {
        this.vessel = vessel;
    }


}