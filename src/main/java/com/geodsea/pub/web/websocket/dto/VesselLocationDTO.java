package com.geodsea.pub.web.websocket.dto;

import com.geodsea.pub.web.rest.dto.VesselDTO;
import org.geojson.Point;

import java.util.Date;

/**
 * A location report for a vessel.
 */
public class VesselLocationDTO {


    private VesselDTO vessel;

    /**
     * The location of the report.
     */
    private Point point;

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
    private Integer accuracy;

    /**
     * The time when this report was received.
     */
    private Date time;

    /**
     * Direction, in degrees, if included with the last location report is a number in the range
     * <code>0.0 &lt; bearing &lt;= 360.0</code>.
     * <p>
     * Bearing is the horizontal direction of travel of this device, and is not related to the device orientation.
     * </p>
     * TODO in android app ensure that 0.0 is converted to null, range is 0.0 < bearing <= 360.0
     */
    private Float bearing;

    /**
     * Speed in metres per second of the vessel at that time.
     * <p>
     * If stationary then speed will be zero. If no speed was reported by client then this will be null.
     * </p>
     */
    private Float speedMetresSec;

    public VesselLocationDTO() {
    }

    public VesselLocationDTO(VesselDTO vessel, Point point, Integer accuracy, Date time, Float bearing,
                             Float speedMetresSec) {
        this.vessel = vessel;
        this.point = point;
        this.accuracy = accuracy;
        this.time = time;
        this.bearing = bearing;
        this.speedMetresSec = speedMetresSec;
    }

    public VesselDTO getVessel() {
        return vessel;
    }

    public void setVessel(VesselDTO vessel) {
        this.vessel = vessel;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

}
