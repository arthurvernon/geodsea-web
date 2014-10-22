package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

/**
 * The GPS location and date/time when the the skipper's mobile device was provided a location update.
 * <p>
 * The time reported may lag this substantially, and this is noted in the {@link #timeReceived} time.
 * </p>
 */
@Embeddable
public class LocationTime implements Comparable<LocationTime> {

    /**
     * The region over which the sea rescue authority operates.
     */
    @Column(name = "LOCATION", nullable = false)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point location;

    /**
     * The date/time when the boat (actually the skipper's mobile device) was at that location.
     */
    @Column(name = "GPS_SIGNAL_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date gpsSignalTime;

    /**
     * The date/time when this particular the report was received by the system.
     * <p>
     * This may lag the {@link #gpsSignalTime} by seconds or minutes depending upon the required reporting
     * frequency. If the mobile device is out of range then it may be some time until reports are submitted.
     * </p>
     */
    @Column(name = "TIME_RECEIVED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeReceived;

    /**
     * The accuracy in metres of the reported location or null if no accuracy information is available.
     * <p>As defined by the android API...<br>
     * <p/>
     * We define accuracy as the radius of 68% confidence. In other words, if you draw a circle centered at this
     * location's latitude and longitude, and with a radius equal to the accuracy, then there is a 68% probability that
     * the true location is inside the circle.
     * In statistical terms, it is assumed that location errors are random with a normal distribution, so the 68%
     * confidence circle represents one standard deviation. Note that in practice, location errors do not always follow
     * such a simple distribution.
     * This accuracy estimation is only concerned with horizontal accuracy, and does not indicate the accuracy of
     * bearing, velocity or altitude if those are included in this Location.
     * </p>
     */
    @Column(name = "ACCURACY", nullable = true)
    private Integer accuracy;

    /**
     * Direction, in degrees, if included with the last location report is a number in the range
     * <code>0.0 &lt; bearing &lt;= 360.0</code>.
     * <p>
     * Bearing is the horizontal direction of travel of this device, and is not related to the device orientation.
     * </p>
     */
    @Column(name = "BEARING", nullable = true)
    private Float bearing;

    /**
     * Speed in metres per second of the vessel at that time.
     * <p>
     * If stationary then speed will be zero. If no speed was reported by client then this will be null.
     * </p>
     */
    @Column(name = "SPEED", nullable = true)
    private Float speedMetresSec;

    /**
     * Create a report of a position at a point in time.
     * <p>
     * Received time is assumed to be now.
     * </p>
     *
     * @param location       the location that this report pertains to.
     * @param gpsSignalTime  the date/time when the GPS location report was established.
     * @param timeReceived   the time when the report was received by the server
     * @param accuracy       reported accuracy per android spec if available, null otherwise
     * @param bearing        possibly null value in degrees s.t. <code>0.0 &lt; bearing &lt;= 360.0</code>
     * @param speedMetresSec optional speed of the vessel at this time
     */
    public LocationTime(Point location, Date gpsSignalTime, Date timeReceived, Integer accuracy, Float bearing, Float speedMetresSec) {
        this.gpsSignalTime = gpsSignalTime;
        this.location = location;
        this.timeReceived = timeReceived;
        this.accuracy = accuracy;
        this.bearing = bearing;
        this.speedMetresSec = speedMetresSec;
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

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Float getBearing() {
        return bearing;
    }

    public void setBearing(Float bearing) {
        this.bearing = bearing;
    }

    public Float getSpeedMetresSec() {
        return speedMetresSec;
    }

    public void setSpeedMetresSec(Float speedMetresSec) {
        this.speedMetresSec = speedMetresSec;
    }

    @Override
    public int compareTo(LocationTime o) {
        return gpsSignalTime.compareTo(o.gpsSignalTime);
    }


}
