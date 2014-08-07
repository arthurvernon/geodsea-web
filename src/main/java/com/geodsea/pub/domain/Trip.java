package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * A plan submitted by a boat (skipper) at the outset of a trip/journey.
 * <p>
 * The plan originates as a starting location, may define some intermediate points, and finishes at an endpoint.
 * The endpoint is typically the same as the start point, but need not be, e.g. in the case of a yacht race
 * or where there is a planned stopover and a new trip at some later time.
 * </p>
 */
@Entity
@Table(name = "T_TRIP", schema = "BOAT")
public class Trip {

    @Id
    @GeneratedValue(generator = "TRIP_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "TRIP_GEN", sequenceName = "TRIP_ID_SEQ")
    private long gid;


    /**
     * A historical record of the journey that is maintained when the user enables tracking.
     */
    @Column(name = "JOURNEY", nullable = true)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private LineString journey;

    /**
     * The start and end point of a trip with any number of points inbetween.
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
    private MultiPoint wayPoints;

    /**
     * A useful precis of the trip, entered probably after the fact.
     */
    @Column(name = "SUMMARY", nullable = true)
    private String summary;


    /**
     * The number of people including the skipper who are on the boat.
     * <p>The maximum number is not constrained, not even by the {@link LicenseVessel#maxPeople}.</p>
     */
    @Column(name = "PEOPLE_ON_BOARD", nullable = true)
    @Min(1)
    private Integer peopleOnBoard;

    /**
     * When this trip is planned to start.
     */
    @Column(name = "SCHEDULED_START", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledStartTime;

    /**
     * When this trip actually started (if the fact was recorded).
     */
    @Column(name = "ACTUAL_START", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualStartTime;


    /**
     * The estimated number of liters of fuel on board at the start of the journey
     */
    @Column(name = "FUEL_L", nullable = true)
    private Integer fuelOnBoard;

    /**
     * The reason for making the trip, e.g. weekend at Rottnest.
     * <p>
     * This information is shared with the rescue authority.
     * </p>
     */
    @Column(name = "HEADLINE", nullable = false, length = 100)
    private String headline;


    /**
     * The time in the plan when the journey is intended to be completed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SCHEDULED_END", nullable = true)
    private Date scheduledEndTime;


    /**
     * The organisation that the system has ascertained is responsible for monitoring this trip.
     */
    @ManyToOne(optional = true)
    @JoinColumn(name = "ROLE_RESCUE_ID", nullable = true)
    private Rescue rescue;

    /**
     * The date when the plan was first advised to sea rescue.
     */
    @Column(name = "ADVISED_FIRST", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date planFirstAdvised;

    /**
     * The date when the plan was last advised to sea rescue.
     * <p>
     * This will be different from when the plan was first advised if the details of the journey have been amended.
     * </p>
     */
    @Column(name = "ADVISED_LAST", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date planLastAdvised;

    /**
     * The actual time, if ever, that the journey was logged as completed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ACTUAL_END", nullable = false)
    private Date actualEndTime;

    /**
     * Who is the skipper for this trip.
     * <p>
     *     The skipper must be identified so that the trip record is associated with at least one person.
     * </p>
     */
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private Skipper skipper;

    /**
     * The particular vessel that is intended to be used for this trip.
     * <p>
     *     The vessel may change but rarely would.
     * </p>
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "VESSEL_ID", nullable = false)
    private Vessel vessel;

    public Trip() {
        super();
    }

    public long getGid() {
        return gid;
    }

    public void setGid(long gid) {
        this.gid = gid;
    }

    public LineString getJourney() {
        return journey;
    }

    public void setJourney(LineString journey) {
        this.journey = journey;
    }

    public MultiPoint getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(MultiPoint wayPoints) {
        this.wayPoints = wayPoints;
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

    public Rescue getRescue() {
        return rescue;
    }

    public void setRescue(Rescue rescue) {
        this.rescue = rescue;
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
}