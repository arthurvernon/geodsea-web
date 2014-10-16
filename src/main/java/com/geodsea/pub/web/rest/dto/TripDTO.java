package com.geodsea.pub.web.rest.dto;

import org.geojson.Feature;
import org.geojson.GeoJsonObject;

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
    private Date actualStart_dt;

    /**
     * The time in the plan when the journey is intended to be completed.
     */
    private Date scheduledEnd_dt;

    /**
     * The actual time, if ever, that the journey was logged as completed.
     */
    private Date actualEnd_dt;


    private GeoJsonObject wayPoints;

    public TripDTO(Long id, RescueOrganisationDTO rescueOrganisation, String headline, Integer peopleOnBoard,
                   Integer fuelOnBoard, Date actualStartTime, Date scheduledEndTime, Date actualEndTime, Feature wayPoints) {
        this.id = id;
        this.rescueOrganisation = rescueOrganisation;
        this.wayPoints = wayPoints;
        this.headline = headline;
        this.peopleOnBoard = peopleOnBoard;
        this.fuelOnBoard = fuelOnBoard;
        this.actualStart_dt = actualStartTime;
        this.scheduledEnd_dt = scheduledEndTime;
        this.actualEnd_dt = actualEndTime;
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

    public Date getActualStart_dt() {
        return actualStart_dt;
    }

    public Date getScheduledEnd_dt() {
        return scheduledEnd_dt;
    }

    public Date getActualEnd_dt() {
        return actualEnd_dt;
    }

    public GeoJsonObject getWayPoints() {
        return wayPoints;
    }
}

