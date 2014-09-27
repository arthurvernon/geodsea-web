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
public class TripSkipperDTO extends TripDTO {


    /**
     * Who is the skipper for this trip.
     * <p>
     * The skipper must be identified so that the trip record is associated with at least one person.
     * </p>
     */
    private ParticipantDTO skipper;

    /**
     * The particular vessel that is intended to be used for this trip.
     * <p>
     * The vessel may change but rarely would.
     * </p>
     */
    private VesselDTO vessel;

    /**
     * A useful precis of the trip, entered probably after the fact.
     */
    private String summary;


    /**
     * The time in the plan when the journey is intended to start.
     */
    private Date scheduledStartTime;


    public TripSkipperDTO() {
        super();
    }

    public TripSkipperDTO(Long id, ParticipantDTO skipper, VesselDTO vessel, RescueOrganisationDTO rescueOrganisation,
                          String headline, String summary, Integer peopleOnBoard, Integer fuelOnBoard, Date scheduledStartTime,
                          Date actualStartTime, Date scheduledEndTime, Date actualEndTime) {
        super(id,rescueOrganisation, headline, peopleOnBoard, fuelOnBoard, actualStartTime, scheduledEndTime, actualEndTime);
        this.skipper = skipper;
        this.vessel = vessel;
        this.summary = summary;
        this.scheduledStartTime = scheduledStartTime;
    }


    public Date getScheduledStartTime() {
        return scheduledStartTime;
    }

    public String getSummary() {
        return summary;
    }

    public VesselDTO getVessel() {
        return vessel;
    }

    public ParticipantDTO getSkipper() {
        return skipper;
    }
}