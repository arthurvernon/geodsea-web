package com.geodsea.pub.web.rest.dto;

import com.geodsea.pub.domain.Vessel;

import java.util.Date;

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
     * The skipper must be identified so that the trip record is associated with at least one person.
     * </p>
     */
    private ParticipantDTO person;

    /**
     * The particular vessel that is intended to be used for this trip.
     * <p>
     * The vessel may change but rarely would.
     * </p>
     */
    private Vessel vessel;


    public TripDTO() {
        super();
    }

    /**
     * Create a trip with the minimum required information.
     *
     * @param vessel
     * @param person
     */
    public TripDTO(Vessel vessel, ParticipantDTO person, String headline) {
        this.vessel = vessel;
        this.person = person;
        this.headline = headline;
    }

    public long getId() {
        return id;
    }


    public String getSummary() {
        return summary;
    }


    public Integer getPeopleOnBoard() {
        return peopleOnBoard;
    }


    public Date getScheduledStartTime() {
        return scheduledStartTime;
    }


    public Date getActualStartTime() {
        return actualStartTime;
    }


    public Integer getFuelOnBoard() {
        return fuelOnBoard;
    }


    public String getHeadline() {
        return headline;
    }

    public Date getScheduledEndTime() {
        return scheduledEndTime;
    }

    public Date getPlanFirstAdvised() {
        return planFirstAdvised;
    }

    public Date getPlanLastAdvised() {
        return planLastAdvised;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RescueOrganisationDTO getRescueOrganisation() {
        return rescueOrganisation;
    }


    public Vessel getVessel() {
        return vessel;
    }

    public ParticipantDTO getPerson() {
        return person;
    }
}