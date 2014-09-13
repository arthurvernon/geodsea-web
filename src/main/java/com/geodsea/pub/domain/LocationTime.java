package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * The GPS location and date/time when the the skipper's mobile device was provided a location update.
 * <p>
 *     The time reported may lag this substantially, and this is noted in the {@link #timeReceived} time.
 * </p>
 */
@Embeddable
public class LocationTime implements Comparable<LocationTime>{

    /**
     * The region over which the sea rescue authority operates.
     */
    @Column(name = "LOCATION", nullable = false)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point location;

    /**
     * The date/time when the boat (actually the skipper's mobile device) was at that location.
     */
    @Column(name="GPS_SIGNAL_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date gpsSignalTime;

    /**
     * The date/time when this particular the report was received by the system.
     * <p>
     *     This may lag the {@link #gpsSignalTime} by seconds or minutes depending upon the required reporting
     *     frequency. If the mobile device is out of range then it may be some time until reports are submitted.
     * </p>
     */
    @Column(name="TIME_RECEIVED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeReceived;


    /**
     * Create a report of a position at a point in time.
     * <p>
     *     Received time is assumed to be now.
     * </p>
     * @param location the location that this report pertains to.
     * @param gpsSignalTime the date/time when the GPS location report was established.
     */
    public LocationTime(Point location, Date gpsSignalTime) {
        this(location, gpsSignalTime, new Date());
    }

    /**
     * Create a report of a position at a point in time.
     * <p>
     *     Received time is assumed to be now.
     * </p>
     * @param location the location that this report pertains to.
     * @param gpsSignalTime the date/time when the GPS location report was established.
     * @param timeReceived the time when the report was received by the server
     */
    public LocationTime(Point location, Date gpsSignalTime, Date timeReceived) {
        this.gpsSignalTime = gpsSignalTime;
        this.location = location;
        this.timeReceived = timeReceived;
    }

    public LocationTime() {
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Date getGpsSignalTime() {
        return gpsSignalTime;
    }

    public void setGpsSignalTime(Date gpsSignalTime) {
        this.gpsSignalTime = gpsSignalTime;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Date timeReceived) {
        this.timeReceived = timeReceived;
    }

    @Override
    public int compareTo(LocationTime o) {
        return gpsSignalTime.compareTo(o.gpsSignalTime);
    }
}
