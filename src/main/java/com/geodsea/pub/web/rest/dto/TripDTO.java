package com.geodsea.pub.web.rest.dto;

import java.util.Date;

/**
 * Created by Arthur Vernon on 23/09/2014.
 */
public class TripDTO {


    private Long id;
    /**
     * The organisation that the system has ascertained is responsible for monitoring this trip.
     */
    private RescueOrganisationDTO rescueOrganisation;

    /**
     * The reason for making the trip, e.g. weekend at Rottnest.
     * <p>
     * This information is shared with the rescue authority - up to 100 characters, must not be null.
     * </p>
     */
    private String headline;

    /**
     * The number of people including the skipper who are on the boat.
     * <p>The maximum number is not constrained, not even by the {@link com.geodsea.pub.domain.LicenseVessel#maxPeople}.</p>
     */
    private Integer peopleOnBoard;

    /**
     * Optional amount of fuel
     */
    private Integer fuelOnBoard;

    /**
     * When this trip actually started (if the fact was recorded).
     */
    private Date actualStartTime;

    /**
     * The time in the plan when the journey is intended to be completed.
     */
    private Date scheduledEndTime;

    /**
     * The actual time, if ever, that the journey was logged as completed.
     */
    private Date actualEndTime;


    public TripDTO(Long id, RescueOrganisationDTO rescueOrganisation, String headline, Integer peopleOnBoard,
                   Integer fuelOnBoard, Date actualStartTime, Date scheduledEndTime, Date actualEndTime) {
        this.id = id;
        this.rescueOrganisation = rescueOrganisation;
        this.headline = headline;
        this.peopleOnBoard = peopleOnBoard;
        this.fuelOnBoard = fuelOnBoard;
        this.actualStartTime = actualStartTime;
        this.scheduledEndTime = scheduledEndTime;
        this.actualEndTime = actualEndTime;
    }

    public TripDTO() {
    }

    public Long getId() {
        return id;
    }

    public RescueOrganisationDTO getRescueOrganisation() {
        return rescueOrganisation;
    }

    public String getHeadline() {
        return headline;
    }

    public Integer getPeopleOnBoard() {
        return peopleOnBoard;
    }

    public Integer getFuelOnBoard() {
        return fuelOnBoard;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public Date getScheduledEndTime() {
        return scheduledEndTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }
}
