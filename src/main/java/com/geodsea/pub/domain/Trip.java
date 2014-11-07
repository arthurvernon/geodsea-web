package com.geodsea.pub.domain;

import com.geodsea.pub.service.util.TripCreateChecks;
import com.vividsolutions.jts.geom.LineString;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

/**
 * A trip created by the skipper.
 * <p>
 * The plan originates at some starting location, may define some intermediate points, and finishes at an endpoint.
 * The endpoint is typically the same as the start point, but need not be, e.g. in the case of a yacht race
 * or where there is a planned stopover and a new trip at some later time.
 * </p>
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "T_TRIP", schema = "BOAT")
public class Trip extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(generator = "TRIP_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "TRIP_GEN", sequenceName = "BOAT.TRIP_ID_SEQ")
    @Column(name="ID")
    private long id;



    /**
     * The start and end point of a trip with any number of points in-between.
     * <p>
     * Journey which may be represented in a minimal form as a departure point but could
     * be as comprehensive as a linestring where the journey commences at the <code>ST_StartPoint(journey)</code>
     * and finishes somewhere else after traversing a series of points (e.g. to a defined point like a marker) or polygons
     * (e.g. a generic reference to Thompson Bay at Rottnest).</p>
     * <p>For the purpose of storing a plan, where a reference is made to a polygon, then this will be converted into
     * a point using the st_centroid(geometry) function.</p>
     * <p>
     * In the case of a yacht race or similar this would be a copy of the event way points.
     * </p>
     */
    @Column(name = "WAY_POINTS", nullable = true)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private LineString wayPoints;


    /**
     * The number of people including the skipper who are on the boat.
     * <p>The maximum number is not constrained, not even by the {@link LicenseVessel#maxPeople}.</p>
     */
    @Column(name = "PEOPLE_ON_BOARD", nullable = true)
    @Min(1)
    @Max(999)
    private Integer peopleOnBoard;


    /**
     * When this trip actually started (if the fact was recorded).
     */
    @Column(name = "ACTUAL_START", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @Future(groups = TripCreateChecks.class)
    private Date actualStartTime;


    /**
     * The estimated number of liters of fuel on board at the start of the journey
     */
    @Column(name = "FUEL_L", nullable = true)
    @Min(0)
    @Max(10000)
    private Integer fuelOnBoard;

    /**
     * The reason for making the trip, e.g. weekend at Rottnest.
     * <p>
     * This information is shared with the rescue authority.
     * </p>
     */
    @Column(name = "HEADLINE", nullable = false, length = 100)
    @Size(min = 2, max = 100)
    @NotNull
    private String headline;


    /**
     * The time in the plan when the journey is intended to be completed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SCHEDULED_END", nullable = true)
    @Future(groups = TripCreateChecks.class)
    private Date scheduledEndTime;


    /**
     * The organisation that the system has ascertained is responsible for monitoring this trip.
     */
    @ManyToOne(optional = true)
    @JoinColumn(name = "RESCUE_ID", nullable = true)
    private Rescue rescue;

    /**
     * The actual time, if ever, that the journey was logged as completed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTUAL_END", nullable = false)
    private Date actualEndTime;


    public Trip() {
        super();
    }

    /**
     * Create a plan for the trip
     * @param headline a summary of the purpose of the trip, shared with SRO
     * @param scheduledEndTime planned end time for the trip
     * @param wayPoints optional way points for journey, beginning with the starting location
     * @param fuel (optional) number of litres of fuel. Required if vessel is motorised.
     * @param people number of people on board
     */
    public Trip(String headline, Date actualStartTime, Date scheduledEndTime, LineString wayPoints, Integer fuel, int people) {
        this.headline = headline;
        this.actualEndTime = actualStartTime;
        this.scheduledEndTime = scheduledEndTime;
        this.wayPoints = wayPoints;
        this.fuelOnBoard = fuel;
        this.peopleOnBoard = people;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LineString getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(LineString wayPoints) {
        this.wayPoints = wayPoints;
    }

    public Integer getPeopleOnBoard() {
        return peopleOnBoard;
    }

    public void setPeopleOnBoard(Integer peopleOnBoard) {
        this.peopleOnBoard = peopleOnBoard;
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

    public Rescue getRescue() {
        return rescue;
    }

    public void setRescue(Rescue rescue) {
        this.rescue = rescue;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

}